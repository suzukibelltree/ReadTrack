package com.belltree.readtrack.screenshot

import com.dropbox.differ.SimpleImageComparator
import com.github.takahirom.roborazzi.RoborazziOptions

/**
 * スクリーンショットテスト共通の Roborazzi オプション。
 *
 * ゴールデン画像を記録する環境(macOS)と CI(Linux)とで、角丸などのアンチエイリアスに
 * 1px あたり最大 0.0136 程度の色差が出るため、1px ごとの許容色差をデフォルト(0.007)から広げる。
 * 差分ピクセル数の割合(changeThreshold)は 0 のままとし、レイアウトや文言の変化は 1px でも検出する。
 */
val screenshotRoborazziOptions = RoborazziOptions(
    compareOptions = RoborazziOptions.CompareOptions(
        imageComparator = SimpleImageComparator(maxDistance = 0.02F)
    )
)
