package com.belltree.readtrack.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.belltree.readtrack.core.PermissionManager
import com.belltree.readtrack.data.local.datastore.isFirstLaunch
import com.belltree.readtrack.ui.setting.HowToUseDialog

/**
 * アプリの初期化処理を管理する
 */
@Composable
fun AppInitializer(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    var showHowToUseDialog by remember { mutableStateOf(false) }

    // 権限管理
    PermissionManager()

    // 初回起動時のダイアログ表示
    LaunchedEffect(Unit) {
        val isFirstLaunch = isFirstLaunch(context)
        if (isFirstLaunch) {
            showHowToUseDialog = true
        }
    }

    if (showHowToUseDialog) {
        HowToUseDialog(onDismiss = { showHowToUseDialog = false })
    }

    // メインコンテンツ
    content()
}