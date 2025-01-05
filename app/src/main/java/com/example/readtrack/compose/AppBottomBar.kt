package com.example.readtrack.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.readtrack.R
import com.example.readtrack.Route

/**
 * アプリのボトムバー
 * @param navController ナビゲーションコントローラー
 */
@Composable
fun BottomBar(navController: NavController) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Spacer(modifier = Modifier.weight(0.5f))
        Column(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { navController.navigate(Route.Home) }) {
                Image(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            }
            Text("ホーム", fontSize = 12.sp, color = Color.Black)
        }
        Spacer(modifier = Modifier.weight(0.5f))
        Column(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { navController.navigate(Route.Library) }) {
                Image(
                    painter = painterResource(id = R.drawable.library),
                    contentDescription = "Library",
                    modifier = Modifier.size(24.dp)
                )
            }
            Text("ライブラリ", fontSize = 12.sp, color = Color.Black)
        }
        Spacer(modifier = Modifier.weight(0.5f))
        Column(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { navController.navigate(Route.Setting) }) {
                Image(
                    painter = painterResource(id = R.drawable.setting),
                    contentDescription = "Setting",
                    modifier = Modifier.size(24.dp)
                )
            }
            Text("設定", fontSize = 12.sp, color = Color.Black)
        }
        Spacer(modifier = Modifier.weight(0.5f))
    }
}