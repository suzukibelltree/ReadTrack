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
	@echo ""
	@echo "===== 起動時間計測結果 (timeToInitialDisplayMs, ms) ====="
	@for f in macrobenchmark/build/outputs/connected_android_test_additional_output/benchmark/connected/*/additionaltestoutput.benchmark.message_*.txt; do \
		name=$$(basename "$$f" .txt | sed 's/^additionaltestoutput\.benchmark\.message_//' | awk -F. '{print $$(NF-1)"."$$NF}'); \
		stats=$$(grep -oE '(min|median|max) [0-9.]+' "$$f" | paste -sd, - | sed 's/,/, /g'); \
		printf '%-40s %s\n' "$$name" "$$stats"; \
	done
	@echo ""
	@echo "詳細: macrobenchmark/build/outputs/androidTest-results/connected/"