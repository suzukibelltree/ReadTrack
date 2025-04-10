package com.belltree.readtrack.themecolor

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.belltree.readtrack.R
import com.belltree.readtrack.ui.theme.DarkBlue
import com.belltree.readtrack.ui.theme.DarkGreen
import com.belltree.readtrack.ui.theme.DarkRed
import com.belltree.readtrack.ui.theme.LightBlue
import com.belltree.readtrack.ui.theme.LightGreen
import com.belltree.readtrack.ui.theme.LightRed


// テーマカラー(アプリバー、グラフに使用)を返す関数
@Composable
fun getPrimaryColor(isDark: Boolean, theme: String): Color {
    return if (isDark) {
        when (theme) {
            stringResource(R.string.setting_theme_color_red) -> DarkRed
            stringResource(R.string.setting_theme_color_green) -> DarkGreen
            else -> DarkBlue
        }
    } else {
        when (theme) {
            stringResource(R.string.setting_theme_color_red) -> LightRed
            stringResource(R.string.setting_theme_color_green) -> LightGreen
            else -> LightBlue
        }
    }
}