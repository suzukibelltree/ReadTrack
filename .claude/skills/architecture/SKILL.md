---
name: architecture
description: Clean Architectureのレイヤー規約、BindingModel/Converterパターン、UseCase作成規約。新規画面の実装・既存画面の改修・UseCase/Repositoryの作成時に必ず参照する。
---

# アーキテクチャ規約

依存方向は UI → Domain → Data。Domain層(`domain/`)はAndroid依存を持たない。

## 実装ルール

### BindingModel / Converter パターン(必須)

新規画面の実装・既存画面の改修では、ドメインモデルを直接 Composable に渡さない。

- 画面ごとに UI 専用のデータモデル **`XxxBindingModel`** を `ui/<screen>/` に定義する
- 変換ロジックは **`XxxBindingModelConverter`**(`object`)に集約する
- ViewModel が UseCase から受け取ったドメインモデルを Converter で BindingModel に変換し、UiState に格納して公開する

既存例: `ui/home/HomeBindingModel.kt` + `HomeBindingModelConverter.kt`

### UseCase 規約

- 単一責任の原則を徹底する: 1 UseCase = 1 ユースケース。複数の関心事を持たせない
- 呼び出しインターフェースは **`suspend operator fun invoke(...)`** に統一する(公開メソッドはこれ1つのみ)
- 命名は「動詞 + 対象 + UseCase」(例: `UpdateBookUseCase`、`GetHomeStaticsUseCase`)
- 配置は `domain/usecase/`。Repository には interface(`domain/repository/`)経由でのみ依存する

### レイヤー間のルール

- ViewModel は Repository を直接触らない。必ず UseCase を経由する
- Repository はデータ取得/保存の抽象化に限定する。ビジネスロジックを持たせない
- DI は Hilt。ViewModel には `@HiltViewModel`、依存は constructor injection

## レビュー観点

- ViewModel にビジネスロジックを書きすぎていないか
- 依存方向が UI → Domain → Data になっているか。具象クラスに依存していないか
- 将来的な機能追加・条件分岐の増加で破綻しない構造か

問題がある場合は「問題点・なぜ問題か・改善案」をセットで提示する。