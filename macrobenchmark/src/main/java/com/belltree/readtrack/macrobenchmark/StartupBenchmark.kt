package com.belltree.readtrack.macrobenchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * アプリ起動時間を計測するベンチマーク
 *
 * 実行方法(要・接続端末):
 * ./gradlew :macrobenchmark:connectedBenchmarkAndroidTest
 *
 * - [startupColdCompilationNone]: JITのみ(プロファイル未適用の最悪ケース)。
 *   Baseline Profile導入時のbefore/after比較の基準になる
 * - [startupColdCompilationDefault]: ストアからのインストール状態に近い既定のコンパイル状態
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupColdCompilationNone() = startup(StartupMode.COLD, CompilationMode.None())

    @Test
    fun startupColdCompilationDefault() = startup(StartupMode.COLD, CompilationMode.DEFAULT)

    @Test
    fun startupWarm() = startup(StartupMode.WARM, CompilationMode.DEFAULT)

    private fun startup(startupMode: StartupMode, compilationMode: CompilationMode) =
        benchmarkRule.measureRepeated(
            packageName = TARGET_PACKAGE,
            metrics = listOf(StartupTimingMetric()),
            iterations = 3,
            startupMode = startupMode,
            compilationMode = compilationMode,
        ) {
            pressHome()
            startActivityAndWait()
        }

    private companion object {
        const val TARGET_PACKAGE = "com.belltree.readtrack"
    }
}