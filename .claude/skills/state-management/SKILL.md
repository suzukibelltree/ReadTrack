---
name: state-management
description: UiState設計、一時イベント(Snackbar等)の扱い、エラーメッセージのリソースID規約。ViewModelの実装・修正、状態管理の設計時に必ず参照する。
---

# 状態管理規約

## 実装ルール

### UiState の形

- 画面ごとに `sealed interface XxxUiState` を定義し、`Loading` / `Success` / `Error` のバリアントを持たせる
- `Success` は BindingModel を保持する(ドメインモデルを直接持たせない)
- ViewModel 内では `private val _uiState = MutableStateFlow<...>` とし、公開は `StateFlow` のみ。MutableState / MutableStateFlow を直接公開しない

### エラーメッセージ・UI文言

- **生の String を UiState やイベントに渡さない。** 必ず `strings.xml` のリソースID(`@StringRes val messageResId: Int`)で統一する
- 例外メッセージ(`localizedMessage` 等)をそのままユーザーに表示しない。ViewModel で適切なリソースIDにマッピングする

```kotlin
data class Error(@StringRes val messageResId: Int) : XxxUiState
```

### 一時イベント(Snackbar 表示・ナビゲーション指示など)

- SharedFlow / Channel は(新規実装では)使わず、**UiState に含める方針**を推奨する（既存は `SettingViewModel.scheduleEvent` 等で SharedFlow を使用しているため段階的に移行）
- UI 側で消費したら、ViewModel のメソッド(例: `consumeEvent()`)を呼んでクリアする

## レビュー観点

- Single Source of Truth: 状態の唯一の管理場所が明確か
- UI State / Domain State が混在していないか
- collect のスコープが適切か(lifecycle 考慮、`collectAsStateWithLifecycle` 等)
- UI でビジネスロジックを持っていないか

必要なら理想的な状態モデルを再設計して提示する。