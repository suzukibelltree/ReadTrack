package com.belltree.readtrack.themecolor

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.belltree.readtrack.ui.theme.DarkContainer
import com.belltree.readtrack.ui.theme.DarkScrim
import com.belltree.readtrack.ui.theme.DarkThemeText
import com.belltree.readtrack.ui.theme.LightContainer
import com.belltree.readtrack.ui.theme.LightScrim
import com.belltree.readtrack.ui.theme.LightThemeText

object AppColors {
    val scrimColor: Color
        @Composable
        get() = if (isSystemInDarkTheme()) DarkScrim else LightScrim

    val textColor: Color
        @Composable
        get() = if (isSystemInDarkTheme()) DarkThemeText else LightThemeText

    val containerColor: Color
        @Composable
        get() = if (isSystemInDarkTheme()) DarkContainer else LightContainer
}