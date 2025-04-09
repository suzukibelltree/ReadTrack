package com.belltree.readtrack.themecolor

import androidx.compose.ui.graphics.Color
import com.belltree.readtrack.ui.theme.DarkBlue
import com.belltree.readtrack.ui.theme.DarkGreen
import com.belltree.readtrack.ui.theme.DarkRed
import com.belltree.readtrack.ui.theme.LightBlue
import com.belltree.readtrack.ui.theme.LightGreen
import com.belltree.readtrack.ui.theme.LightRed


// テーマカラー(アプリバー、グラフに使用)を返す関数
fun getPrimaryColor(isDark: Boolean, theme: String): Color {
    return if (isDark) {
        when (theme) {
            "Red" -> DarkRed
            "Green" -> DarkGreen
            else -> DarkBlue
        }
    } else {
        when (theme) {
            "Red" -> LightRed
            "Green" -> LightGreen
            else -> LightBlue
        }
    }
}