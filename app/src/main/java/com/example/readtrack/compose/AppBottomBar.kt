package com.example.readtrack.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = { navController.navigate(Route.Home) }) {
                    Image(
                        painter = painterResource(id = R.drawable.home),
                        contentDescription = "Home",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = stringResource(id = R.string.screen_home),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Column(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = { navController.navigate(Route.Library) }) {
                    Image(
                        painter = painterResource(id = R.drawable.library),
                        contentDescription = "Library",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = stringResource(id = R.string.screen_library),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Column(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = { navController.navigate(Route.Setting) }) {
                    Image(
                        painter = painterResource(id = R.drawable.setting),
                        contentDescription = "Setting",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = stringResource(id = R.string.screen_setting),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}