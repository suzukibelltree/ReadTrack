package com.belltree.readtrack.compose.RegisterManually

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ManualBookEntryScreen(
    navController: NavController
) {
    Text(text = "書籍情報を手動で入力")
}