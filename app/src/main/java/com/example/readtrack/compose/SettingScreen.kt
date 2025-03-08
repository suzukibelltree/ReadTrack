package com.example.readtrack.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.readtrack.R
import com.example.readtrack.Route
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingScreen(
    navController: NavController
) {
    // TODO: 設定画面の実装
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.setting_character_size),
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { }
        )
        HorizontalDivider()
        Text(
            text = stringResource(R.string.setting_theme_color),
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { }
        )
        HorizontalDivider()
        Text(
            text = stringResource(R.string.setting_switch_dark_light),
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { }
        )
        HorizontalDivider()
        Text(
            text = "ログアウト",
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    FirebaseAuth
                        .getInstance()
                        .signOut()
                    //currentUser = null // ログイン状態をリセット
                    navController.navigate(Route.Login)
                }
        )
        HorizontalDivider()
    }
}