package com.belltree.readtrack.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.belltree.readtrack.R
import com.belltree.readtrack.ui.navigation.Route

/**
 * 本を新しく登録する際の登録手段を選択する画面
 * @param navController ナビゲーションコントローラー
 */
@Composable
fun RegisterProcessScreen(
    navController: NavController
) {
    Column(
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.register_process),
            fontSize = 16.sp,
            fontWeight = FontWeight.Companion.Bold,
            modifier = Modifier.Companion.padding(16.dp)
        )
        Button(
            onClick = { navController.navigate(Route.Search) },
            modifier = Modifier.Companion.padding(16.dp)
        ) {
            Text(text = stringResource(R.string.register_title))
        }
        Button(
            onClick = { navController.navigate(Route.BarcodeScanner) },
            modifier = Modifier.Companion.padding(16.dp)
        ) {
            Text(text = stringResource(R.string.register_barcode))
        }
        Button(
            onClick = { navController.navigate(Route.RegisterManually) },
            modifier = Modifier.Companion.padding(16.dp)
        ) {
            Text(text = "書籍情報を手動で入力")
        }
    }
}