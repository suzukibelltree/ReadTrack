# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build
./gradlew assembleDebug
./gradlew bundleRelease

# Lint
./gradlew lintDebug

# Unit tests (all)
./gradlew test

# Single test class
./gradlew test --tests "com.belltree.readtrack.UpdateBookUseCaseTest"

# Instrumentation tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Release deploy to Google Play
./gradlew clean bundleRelease publishReleaseBundle
```

## Architecture

Clean Architecture + MVVM. Single `:app` module.

```
app/src/main/java/com/belltree/readtrack/
├── di/           # Hilt modules (AppModule, NetworkModule)
├── domain/       # Interfaces & UseCases; no Android deps
│   ├── model/
│   ├── repository/   # interfaces only
│   └── usecase/
├── data/         # Implementations
│   ├── local/room/       # Room DB (BookDao, ReadLogDao)
│   ├── local/datastore/  # DataStore preferences
│   ├── remote/           # Retrofit + Google Books API + Paging 3
│   ├── mapper/
│   └── repository/
├── ui/           # Composables + ViewModels, one package per screen
│   └── navigation/   # NavHost, BottomBar, route definitions
├── core/         # Cross-cutting utilities (notifications via WorkManager)
└── themecolor/   # Runtime theme management
```

**Key patterns:**
- ViewModels expose `StateFlow<UiState>` where `UiState` is a sealed class with `Loading`, `Success`, `Error` variants.
- All data access flows through UseCases; ViewModels never touch repositories directly.
- DI is Hilt throughout; `@HiltViewModel` on every ViewModel.
- Navigation uses type-safe routes: `@Serializable sealed interface Route` in `ui/navigation/ReadTrackScreen.kt`.
- Screens never receive domain models directly; each screen defines a `BindingModel` + `Converter` (see `.claude/skills/architecture/SKILL.md`).

## Development Guidelines (Skills)

Project-specific implementation rules live in `.claude/skills/`:
- `general` — 前提・出力形式(レビュー時/実装時)・Git運用
- `architecture` — レイヤー規約、BindingModel/Converter、UseCase規約
- `state-management` — UiState設計、イベント処理、文言のリソースID規約
- `compose-ui` — Screen/Content分離、components配置、Preview方針
- `testing` — テスト作成基準・配置規約・Roborazzi運用

## Testing Patterns

Tests live in `app/src/test/`. The project uses:
- **MockK** (`coEvery`, `coVerify`) for suspending functions
- **Turbine** (`StateFlow.test { awaitItem() }`) for Flow assertions
- **`kotlinx-coroutines-test`** (`runTest`, `advanceUntilIdle`)
- `MainDispatcherRule` (in test sources) as a `@get:Rule` to swap the main dispatcher

Example shape:
```kotlin
@get:Rule val mainDispatcherRule = MainDispatcherRule()

@Test
fun `some test`() = runTest {
    coEvery { repository.doSomething() } returns Unit
    viewModel.someStateFlow.test {
        val item = awaitItem()
        assertEquals(expected, item)
    }
}
```

## Key Dependencies

| Area | Library |
|------|---------|
| UI | Jetpack Compose + Material 3 |
| Navigation | `navigation-compose` |
| DI | Hilt 2.51.1 |
| DB | Room 2.6.1 |
| Prefs | DataStore |
| Network | Retrofit 2 + OkHttp 4 + Gson |
| Paging | Paging 3 |
| Images | Coil |
| Charts | Vico |
| Camera/Barcode | CameraX + ML Kit |
| Widgets | Glance |
| Background | WorkManager |
| Versions | `gradle/libs.versions.toml` (version catalog) |

Kotlin 2.0.0 · Java 17 · minSdk 26 · targetSdk 35

## CI/CD

GitHub Actions workflows in `.github/workflows/`:
- **android-ci.yml** — PR to main/develop: `assembleDebug` + `lintDebug`
- **android-test.yml** — PR: `./gradlew test`
- **deploy.yml** — push to main: builds release bundle → publishes to Google Play Console
- **update-readme.yml** — updates README version info

Release signing and Play Store credentials are injected via GitHub secrets (`SIGNING_STORE_PASSWORD`, `SIGNING_KEY_ALIAS`, `SIGNING_KEY_PASSWORD`, service account JSON).