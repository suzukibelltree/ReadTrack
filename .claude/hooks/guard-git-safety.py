#!/usr/bin/env python3
"""PreToolUse(Bash) hook: 取り返しのつかない git / gh 操作と、秘密情報の読み出し・混入を実行前に止める。

止めるときは exit 2 で理由と代わりの手段を stderr に返す。Claude はそれを読んでその場で直して進める。
hook は Claude のツール呼び出しにだけ効き、人がターミナルで打つコマンドには影響しない。

## 防ぐ範囲(脅威モデル)

この hook が防ぐのは、Claude の**手違い**(git add -A、force push、秘密ファイルの cat など、
作業中に自然に書きうる形)。止めた理由を返して、その場で正しい書き方に直させるのが目的。

シェルの構文はすべて解釈できないので、**意図的な回避**までは防がない。例:
- 変数を経由した参照(p=local.properties; cat "$p")
- xargs やスクリプトファイルを経由した実行
- この hook が知らない書き方で、同じ意味になるコマンド

破られてはいけないものは、GitHub 側で強制する:
- 秘密情報の push: Secret scanning の Push protection(有効)
- main / develop の force push とブランチの削除: リポジトリの ruleset

## 判定できないとき

- push / PR 作成で、対象のブランチや作業ディレクトリを判定できないときは止める(fail-closed)
- 想定外の入力でこの hook 自体が落ちたときは、作業を止めずに通す(fail-open)。
  CI(claude-review.yml)でもこの設定が読み込まれるため、hook の不具合でレビューを止めないことを優先した
"""

import json
import os
import re
import shlex
import subprocess
import sys

PROTECTED_BRANCHES = {"main", "develop", "master"}
REPLAY_PREFIX = "replay/"

# 中身を Claude の文脈に載せたくない・commit させたくないファイル
SECRET_BASENAMES = {
    "local.properties",
    "google-services.json",
    "play-service-account.json",
    ".envrc",
}
SECRET_SUFFIXES = (".jks", ".keystore", ".p12")

SECRET_PATTERNS = [
    ("Google API キー", re.compile(r"AIza[0-9A-Za-z_\-]{35}")),
    ("秘密鍵", re.compile(r"-----BEGIN (?:RSA |EC |OPENSSH |DSA |)PRIVATE KEY-----")),
    ("サービスアカウントの秘密鍵", re.compile(r'"private_key"\s*:\s*"')),
    ("GitHub トークン", re.compile(r"\b(?:gh[pousr]_[A-Za-z0-9]{36,}|github_pat_[A-Za-z0-9_]{20,})")),
    ("AWS アクセスキー", re.compile(r"\bAKIA[0-9A-Z]{16}\b")),
    ("Slack トークン", re.compile(r"\bxox[abprs]-[A-Za-z0-9-]{10,}")),
    ("Anthropic API キー", re.compile(r"\bsk-ant-[A-Za-z0-9_\-]{20,}")),
    (
        "パスワードの直書き",
        re.compile(r"""(?i)\b(?:store|key)?password\s*[=:]\s*["'][^"'$\s]{4,}["']"""),
    ),
]

# 秘密ファイルの名前が引数に出ても、中身を読まないので通すコマンド
SAFE_COMMANDS_FOR_SECRET_PATHS = {"ls", "test", "["}
# 同じく、中身を出力しない git のサブコマンド。add / commit は個別の検査で秘密ファイルを止める。
# show / diff / grep / cat-file / log などは中身を出せるので、ここには入れない
SAFE_GIT_SUBCOMMANDS_FOR_SECRET_PATHS = {"status", "check-ignore", "ls-files", "rm", "add", "commit"}

# 先頭に付いても、後ろのコマンドをそのまま実行するだけのラッパーと、値を1つ取るそのオプション
COMMAND_WRAPPERS = {
    "env": {"-u", "--unset", "-C", "--chdir"},
    "command": set(),
    "nohup": set(),
    "time": {"-f", "--format", "-o", "--output"},
    "sudo": {"-u", "--user", "-g", "--group", "-h", "--host", "-p", "--prompt", "-C", "-D", "-R", "-T", "-U"},
    "exec": {"-a"},
}
# 後ろのコマンドの前に来ても、実行の意味を変えないシェルの予約語
SHELL_KEYWORDS = {"if", "then", "else", "elif", "fi", "do", "done", "while", "until", "!", "{", "}"}
SHELLS = {"bash", "sh", "zsh", "dash"}
SEPARATOR_CHARS = set(";&|\n()")

# 対象ファイルを引数ではなくファイルから受け取るオプション。パスの検査をすり抜けるので使わせない
PATHSPEC_FILE_OPTS = ("--pathspec-from-file", "--pathspec-file-nul")

# 値を1つ取る git commit の短いオプション(結合された -am などを解釈するため)
COMMIT_SHORT_OPTS_WITH_VALUE = set("mFcCt")


class Block(Exception):
    pass


def is_secret_path(token: str) -> bool:
    # --file=path や HEAD:path の形でも、末尾のパスで判定する
    path = token.strip("'\"").rsplit("=", 1)[-1].rsplit(":", 1)[-1]
    base = os.path.basename(path)
    if base in SECRET_BASENAMES or base.endswith(SECRET_SUFFIXES):
        return True
    return base.endswith(".json") and "service-account" in base


def extract_substitutions(command: str):
    """$(...) とバッククォートの中身を取り出す。シェルはこれらを先に実行するので、中身も検査する。"""
    found = []
    i, n = 0, len(command)
    quote = None
    while i < n:
        c = command[i]
        if quote == "'":
            if c == "'":
                quote = None
        elif c == "\\":
            i += 1
        elif c == "'" and quote is None:
            quote = "'"
        elif c == '"':
            quote = None if quote == '"' else '"'
        elif c == "$" and command.startswith("$(", i):
            depth, j = 1, i + 2
            while j < n and depth:
                if command[j] == "(":
                    depth += 1
                elif command[j] == ")":
                    depth -= 1
                j += 1
            found.append(command[i + 2 : j - 1 if depth == 0 else n])
            i = j
            continue
        elif c == "`":
            end = command.find("`", i + 1)
            end = n if end == -1 else end
            found.append(command[i + 1 : end])
            i = end + 1
            continue
        i += 1
    return found


def tokenize(command: str):
    """コマンドを ; && || | & 改行 ( ) で区切ったセグメントの列にする。"""
    lex = shlex.shlex(command, posix=True, punctuation_chars=";&|\n()")
    lex.whitespace = " \t\r"
    lex.whitespace_split = True
    try:
        tokens = list(lex)
    except ValueError:
        # 閉じていない引用符やヒアドキュメントなど。空白区切りで粗く判定する
        tokens = command.replace("\n", " ; ").split()
    segments, current = [], []
    for tok in tokens:
        if tok and set(tok) <= SEPARATOR_CHARS:
            if current:
                segments.append(current)
            current = []
        else:
            current.append(tok)
    if current:
        segments.append(current)
    return segments


def git(cwd: str, *args: str) -> str:
    result = subprocess.run(
        ["git", "-C", cwd, *args], capture_output=True, text=True, timeout=20
    )
    return result.stdout if result.returncode == 0 else ""


def current_branch(cwd: str) -> str:
    return git(cwd, "rev-parse", "--abbrev-ref", "HEAD").strip()


def scan_secrets(text: str):
    found = []
    for line in text.splitlines():
        if not line.startswith("+") or line.startswith("+++"):
            continue
        for label, pattern in SECRET_PATTERNS:
            if pattern.search(line):
                found.append(label)
    return sorted(set(found))


def is_broad_pathspec(path: str, cwd: str) -> bool:
    """作業ツリー全体や、まとまったファイルを指す指定か。

    glob(*.json)はシェルが展開した後の実ファイルを hook から見られず、
    pathspec の特殊記法(:/ や :(glob)…)は git が解釈するので、どちらも明示的なパスとして扱えない。
    """
    if any(c in path for c in "*?[") or path.startswith(":"):
        return True
    # ".", "./", "../<repo>" など、作業ディレクトリそのものかその親を指すもの
    resolved = os.path.normpath(os.path.join(cwd, os.path.expanduser(path)))
    here = os.path.normpath(cwd)
    return here == resolved or here.startswith(resolved.rstrip(os.sep) + os.sep)


def check_explicit_paths(paths, cwd, action: str):
    broad = [p for p in paths if is_broad_pathspec(p, cwd)]
    if broad:
        raise Block(
            f"{', '.join(broad)} はまとまったファイルを指す指定なので、{action}の対象として使えません"
            "(.idea の変更や秘密情報が混ざる恐れがあるため)。ファイルを1つずつパスで指定してください。"
        )


def check_secret_paths(paths, action: str):
    hits = [p for p in paths if is_secret_path(p)]
    if hits:
        raise Block(
            f"{', '.join(hits)} は秘密情報を含むファイルなので{action}できません。"
            "このファイルは commit の対象から外して続けてください。"
            "本当に必要な変更なら、人が手で commit します。"
        )


def strip_git_globals(args):
    """git のグローバルオプションを読み飛ばし、(-C の値, サブコマンド, 残りの引数) を返す。"""
    target = None
    i = 0
    while i < len(args):
        a = args[i]
        if a == "-C" and i + 1 < len(args):
            target = args[i + 1]
            i += 2
        elif a in ("-c", "--git-dir", "--work-tree", "--namespace") and i + 1 < len(args):
            i += 2
        elif a.startswith("-"):
            i += 1
        else:
            return target, a, args[i + 1 :]
    return target, None, []


def check_add(args, cwd, pending_adds):
    for a in args:
        if a.startswith(PATHSPEC_FILE_OPTS):
            raise Block(
                f"git add {a} は対象のファイルを検査できないので使えません。"
                "commit するファイルを1つずつパスで指定してください。"
            )
        if a in ("-A", "--all", ".", "-u", "--update", "-f", "--force", ":/", "*"):
            raise Block(
                f"git add {a} は意図しないファイル(.idea の変更や秘密情報など)まで"
                "ステージする恐れがあるので使えません。"
                "commit するファイルを1つずつパスで指定してください。"
            )
    paths = [a for a in args if not a.startswith("-")]
    check_explicit_paths(paths, cwd, "ステージ")
    check_secret_paths(paths, "ステージ")
    pending_adds.extend((cwd, p) for p in paths)


# 値としてファイルを読む git commit のオプション
COMMIT_FILE_OPTS = {"-F", "--file", "-t", "--template"}


def check_commit(args, cwd, pending_adds):
    i = 0
    paths = []
    read_files = []
    while i < len(args):
        a = args[i]
        if a.startswith(PATHSPEC_FILE_OPTS):
            raise Block(
                f"git commit {a} は対象のファイルを検査できないので使えません。"
                "git add でファイルを1つずつ指定してから commit してください。"
            )
        if a in ("--no-verify", "-n"):
            raise Block(
                "--no-verify は使えません。hook や検査が落ちた原因を直してから commit してください。"
            )
        if a in ("-a", "--all"):
            raise Block(
                "git commit -a は変更した全ファイルを含めてしまうので使えません。"
                "git add でファイルを1つずつ指定してから commit してください。"
            )
        if a.startswith("--"):
            name, eq, value = a.partition("=")
            if name in ("--file", "--template"):
                if eq:
                    read_files.append(value)
                elif i + 1 < len(args):
                    read_files.append(args[i + 1])
            if not eq and name in ("--message", "--file", "--author", "--date", "--template", "--fixup", "--squash", "--reuse-message", "--reedit-message"):
                i += 1
        elif a.startswith("-") and len(a) > 1:
            for k, ch in enumerate(a[1:], start=1):
                if ch == "a":
                    raise Block(
                        f"git commit {a} は -a(変更した全ファイルを含める)を含むので使えません。"
                        "git add でファイルを1つずつ指定してから commit してください。"
                    )
                if ch in COMMIT_SHORT_OPTS_WITH_VALUE:
                    value = a[k + 1 :]
                    if not value:
                        value = args[i + 1] if i + 1 < len(args) else ""
                        i += 1
                    if "-" + ch in COMMIT_FILE_OPTS:
                        read_files.append(value)
                    break
        else:
            paths.append(a)
        i += 1

    check_secret_paths(read_files, "commit メッセージとして読み込みに")
    check_explicit_paths(paths, cwd, "commit")
    check_secret_paths(paths, "commit")
    staged = git(cwd, "diff", "--cached", "--name-only").split()
    check_secret_paths(staged, "commit")

    # ステージ済みの差分と、同じコマンド内でこれから add / commit するファイルの中身を検査する
    diff = git(cwd, "diff", "--cached", "-U0")
    for add_cwd, path in pending_adds + [(cwd, p) for p in paths]:
        diff += git(add_cwd, "diff", "-U0", "--", path)
        if not git(add_cwd, "ls-files", "--", path).strip():
            full = os.path.join(add_cwd, path)
            if os.path.isfile(full):
                try:
                    with open(full, encoding="utf-8", errors="ignore") as f:
                        diff += "".join("+" + line for line in f)
                except OSError:
                    pass
    found = scan_secrets(diff)
    if found:
        raise Block(
            f"commit しようとしている差分に {', '.join(found)} らしい値があります。"
            "値をコードから外し、local.properties や環境変数(BuildConfig 経由)から読む形にしてください。"
            "誤検知だと判断した場合は、自分で回避せずに作業を止め、人に commit を任せてください。"
        )


def require_branch(cwd, action: str) -> str:
    branch = current_branch(cwd) if cwd else ""
    # detached HEAD(replay モードの worktree など)では rev-parse が "HEAD" を返す
    if not branch or branch == "HEAD":
        raise Block(
            f"作業ディレクトリかブランチを判定できないので、{action}の安全を確かめられません。"
            "cd を使わずに、リポジトリの中で `git -C <パス>` の形で実行してください。"
        )
    return branch


def check_push(args, cwd):
    branch = require_branch(cwd, "push")
    refspecs = []
    for a in args:
        if a in ("-f", "--force", "--mirror", "--all", "--branches", "-d", "--delete", "--prune") or a.startswith(
            ("--force-with-lease", "--force-if-includes")
        ):
            raise Block(
                f"git push {a} は remote の履歴やブランチを壊す恐れがあるので使えません。"
                "履歴を書き換えずに、追加の commit で直してください。"
            )
        if not a.startswith("-"):
            refspecs.append(a)
    # 先頭は remote 名。残りが refspec
    refspecs = refspecs[1:]
    if not refspecs:
        refspecs = [branch]
    for spec in refspecs:
        if spec.startswith("+") or spec.startswith(":"):
            raise Block(
                f"refspec {spec} は force push か remote ブランチの削除になるので使えません。"
            )
        src, _, dst = spec.partition(":")
        dst = dst or src
        if dst == "HEAD":
            dst = branch
        dst = dst.removeprefix("refs/heads/")
        if dst in PROTECTED_BRANCHES:
            raise Block(
                f"{dst} への直接 push はできません。作業ブランチ(feature/… や fix/… など)に push し、"
                "PR を通してマージしてください。"
            )
        if REPLAY_PREFIX in spec or branch.startswith(REPLAY_PREFIX):
            raise Block(
                "replay/ ブランチはハーネスの評価用なので push できません。"
                "評価モードでは PR 本文をファイルに書き出して終えてください。"
            )


def check_gh(args, cwd):
    # サブコマンドより前に置かれたオプション(-R owner/repo など)を読み飛ばす
    positional, i = [], 0
    while i < len(args):
        a = args[i]
        if a in ("-R", "--repo", "--hostname"):
            i += 2
            continue
        if not a.startswith("-"):
            positional.append(a)
        i += 1
    if len(positional) < 2 or positional[0] != "pr":
        return
    sub = positional[1]
    if sub == "merge":
        raise Block("PR のマージは人が行います。ハーネスは draft PR の作成までにしてください。")
    if sub == "create":
        branch = require_branch(cwd, "PR の作成")
        if branch.startswith(REPLAY_PREFIX) or any(REPLAY_PREFIX in a for a in args):
            raise Block(
                "replay/ ブランチはハーネスの評価用なので PR を作れません。"
                "評価モードでは PR 本文をファイルに書き出して終えてください。"
            )


def strip_prefixes(seg, cwd, depth):
    """先頭の環境変数の代入(FOO=bar)、シェルの予約語、env / sudo などのラッパーとそのオプションを読み飛ばす。

    代入の値そのものは追わない(変数を経由した参照は防ぐ範囲の外。冒頭の脅威モデルを参照)。
    """
    while seg:
        head = seg[0]
        name = os.path.basename(head)
        if re.match(r"^[A-Za-z_][A-Za-z0-9_]*=", head) or head in SHELL_KEYWORDS:
            seg = seg[1:]
            continue
        if name not in COMMAND_WRAPPERS:
            break
        value_opts = COMMAND_WRAPPERS[name]
        seg = seg[1:]
        while seg and seg[0].startswith("-") and seg[0] != "-":
            opt = seg[0]
            if opt == "--":
                seg = seg[1:]
                break
            if name == "env" and opt in ("-S", "--split-string") and len(seg) > 1:
                # env -S "git push --force" は文字列を分割して実行する
                check(seg[1], cwd, depth + 1)
                return []
            seg = seg[2:] if opt in value_opts else seg[1:]
    return seg


def resolve_cd(args, cwd):
    """cd の行き先を返す。判定できないときは None(以降の push などは止まる)。"""
    if cwd is None:
        return None
    targets = []
    for i, a in enumerate(args):
        if a == "--":
            targets = args[i + 1 :]
            break
        if a.startswith("-") and a != "-":
            continue  # -P / -L / -e などは行き先を変えない
        targets = args[i:]
        break
    if not targets:
        target = "~"
    else:
        target = targets[0]
    if target == "-" or "$" in target:
        return None
    resolved = os.path.normpath(os.path.join(cwd, os.path.expanduser(target)))
    return resolved if os.path.isdir(resolved) else None


def check(command: str, cwd, depth: int = 0):
    if depth > 5:
        raise Block("コマンドの入れ子が深すぎて検査できません。コマンドを分けて実行してください。")
    for inner in extract_substitutions(command):
        check(inner, cwd, depth + 1)

    pending_adds = []
    for seg in tokenize(command):
        seg = strip_prefixes(seg, cwd, depth)
        if not seg:
            continue
        cmd, args = os.path.basename(seg[0]), seg[1:]

        # bash -c "..." と eval は、引数の文字列をコマンドとして検査する
        if cmd in SHELLS and "-c" in args:
            idx = args.index("-c")
            if idx + 1 < len(args):
                check(args[idx + 1], cwd, depth + 1)
            continue
        if cmd == "eval":
            check(" ".join(args), cwd, depth + 1)
            continue

        if cmd == "cd":
            cwd = resolve_cd(args, cwd)
            continue

        if cmd == "git":
            _, git_sub, _ = strip_git_globals(args)
            reads_secret = git_sub not in SAFE_GIT_SUBCOMMANDS_FOR_SECRET_PATHS
        else:
            reads_secret = cmd not in SAFE_COMMANDS_FOR_SECRET_PATHS
        if reads_secret:
            hits = [a for a in args if is_secret_path(a)]
            if hits:
                raise Block(
                    f"{', '.join(hits)} は API キーや署名鍵などの秘密情報を含むので、中身を読めません。"
                    "値が必要なら、ビルド設定(app/build.gradle.kts)がどこから読むかを確かめ、"
                    "値そのものは扱わずに進めてください。"
                )

        if cmd == "git":
            target, sub, rest = strip_git_globals(args)
            gcwd = cwd
            if target:
                gcwd = os.path.normpath(os.path.join(cwd or "", os.path.expanduser(target)))
                if cwd is None and not os.path.isabs(target):
                    gcwd = None
            if gcwd is None and sub in ("add", "commit"):
                raise Block(
                    "作業ディレクトリを判定できないので、commit の対象を確かめられません。"
                    "cd を使わずに `git -C <パス>` の形で実行してください。"
                )
            if sub == "add":
                check_add(rest, gcwd, pending_adds)
            elif sub == "commit":
                check_commit(rest, gcwd, pending_adds)
            elif sub == "push":
                check_push(rest, gcwd)
        elif cmd == "gh":
            check_gh(args, cwd)


def main():
    try:
        data = json.load(sys.stdin)
        if data.get("tool_name") != "Bash":
            return 0
        command = data.get("tool_input", {}).get("command", "")
        cwd = data.get("cwd") or os.getcwd()
        check(command, cwd)
    except Block as e:
        print(f"[guard-git-safety] {e}", file=sys.stderr)
        return 2
    except Exception as e:  # noqa: BLE001 - hook の不具合で作業を止めない
        print(f"[guard-git-safety] 内部エラーのため検査をスキップしました: {e!r}", file=sys.stderr)
        return 0
    return 0


if __name__ == "__main__":
    sys.exit(main())
