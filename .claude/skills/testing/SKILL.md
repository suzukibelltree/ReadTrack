---
name: testing
description: テストの作成基準・配置規約・記法、スクリーンショットテスト(Roborazzi)の運用。ユニットテストやVRTの作成・修正時に必ず参照する。
---

# テスト規約

## 作成基準(何をテストするか)

- **UseCase / ViewModel:** 追加・修正のたびに一律で必須ではない。**役割とロジックの複雑さに応じて判断する**(Repository への単純な委譲だけなら省略可。分岐・変換・状態遷移を持つなら作成する)
- **Repository:** 現状テストは存在しない。必須ではないが、今後拡充していきたい意向があるため、追加は歓迎
- **スクリーンショットテスト:** 新規画面で一律必須ではない。**表示頻度が高い画面か・UIが安定しているか**を基準に判断する

## 配置規約

- ユニットテストは `app/src/test/java/com/belltree/readtrack/` **直下にフラットに置く**(パッケージ階層をミラーしない。これは意図した規約)
- スクリーンショットテストは `app/src/testDebug/java/com/belltree/readtrack/screenshot/` に置く(**testDebug 必須**: `createComposeRule` が使う ComponentActivity は debug 限定の `ui-test-manifest` 提供のため、`src/test` に置くと testReleaseUnitTest が落ちて CI が失敗する)
- ゴールデン画像は `app/src/test/snapshots/images/` に commit する

## ユニットテストの記法

- **MockK**(`coEvery` / `coVerify`)、**Turbine**(`flow.test { awaitItem() }`)、`kotlinx-coroutines-test`(`runTest`、`advanceUntilIdle`)
- `@get:Rule val mainDispatcherRule = MainDispatcherRule()` で main dispatcher を差し替える
- テスト名は英語のバッククォート形式(例: `` fun `emits Success when use case succeeds`() ``)
- 検証は正常系・異常系・境界値をカバーする
- 外部依存はモック化するが、過剰なモックや実装依存のテスト(内部実装の呼び出し順序への依存など)は避ける

## スクリーンショットテスト(Roborazzi)の運用

```bash
# ゴールデン画像を記録(初回・意図したUI変更後)
./gradlew :app:recordRoborazziDebug --tests "com.belltree.readtrack.screenshot.*"

# 比較検証(CI で使用)
./gradlew :app:verifyRoborazziDebug --tests "com.belltree.readtrack.screenshot.*"

# 差分レポート生成(HTML)
./gradlew :app:compareRoborazziDebug
```

注意点:

- `@Config(application = Application::class)` が必須(`ReadTrackApplication.onCreate()` が WorkManager を起動しようとするため)
- テスト対象は ViewModel に依存しない stateless な `XxxScreenContent` にする
- `thumbnail = null` にして Coil のネットワーク呼び出しを回避する
- `ReadTrackTheme(dynamicColor = false)` でラップする(配色をアプリ定義に固定し、Robolectric のシステムリソース由来の色への依存を避ける)