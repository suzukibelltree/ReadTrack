---
name: compose-ui
description: Composableの構成規約(Screen/Contentの分離、NavControllerの扱い、components配置、Preview方針)。画面UIの実装・修正時に必ず参照する。
---

# Compose UI 規約

## 実装ルール

### 画面 Composable の標準構成(現状コードの方針を正とする)

画面は2層に分ける:

```kotlin
// 1. エントリポイント: NavController と ViewModel を受け取る薄いラッパー
@Composable
fun XxxScreen(
    navController: NavController,
    viewModel: XxxViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    XxxScreenContent(
        uiState = uiState,
        onBookClick = { bookId -> navController.navigate(Route.MyBook(bookId)) },
    )
}

// 2. 本体: stateless で ViewModel/NavController に依存しない(internal)
@Composable
internal fun XxxScreenContent(
    uiState: XxxUiState,
    onBookClick: (String) -> Unit,
) { ... }
```

- `NavController` は `XxxScreen` までで止める。`ScreenContent` 以下にはラムダで hoist して渡す
- `ScreenContent` を ViewModel 非依存にするのは、スクリーンショットテスト(Roborazzi)の対象にするため
- ナビゲーションのルートは `ui/navigation/ReadTrackScreen.kt` の `@Serializable sealed interface Route` に追加する(type-safe navigation)

### コンポーネントの配置

- 複数画面で再利用するコンポーネントは **`ui/components/`** に置く(この方針でディレクトリを新設してよい)
- 画面固有のコンポーネントは各画面パッケージ(`ui/<screen>/`)に置く

### Preview

- `@Preview` は必須ではない。付ける場合は代表的な状態のみでよい

## レビュー観点

- state hoisting が適切に行われているか。Composable 内に不要な状態を持っていないか
- 不要な再コンポジションが発生していないか。`remember` / `derivedStateOf` の使用が適切か
- Composable が肥大化していないか。再利用可能な単位に分割されているか

問題があれば改善コードも提示する。