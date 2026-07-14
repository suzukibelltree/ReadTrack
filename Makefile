# ReadTrack 開発用コマンド
# Gradle実行にはAndroid Studio同梱のJBRを使用する

JAVA_HOME_PATH := /Applications/Android Studio.app/Contents/jbr/Contents/Home
GRADLEW := JAVA_HOME="$(JAVA_HOME_PATH)" sh ./gradlew
ADB := $(shell command -v adb 2>/dev/null || echo "$(HOME)/Library/Android/sdk/platform-tools/adb")

.PHONY: benchmark

# パフォーマンス測定(Macrobenchmark)を実行する。実機またはエミュレータの接続が必要
benchmark:
	@"$(ADB)" devices | grep -q "device$$" || { echo "エラー: 接続中の端末がありません。実機またはエミュレータを接続してください"; exit 1; }
	$(GRADLEW) :macrobenchmark:connectedBenchmarkAndroidTest
	@echo "計測結果: macrobenchmark/build/outputs/androidTest-results/connected/"