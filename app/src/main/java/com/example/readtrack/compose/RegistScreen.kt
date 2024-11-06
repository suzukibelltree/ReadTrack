package com.example.readtrack.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterProcessScreen(){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "登録方法を選択してください。",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        Button(
            onClick = { },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("タイトルを検索して登録")
        }
        Button(
            onClick = { },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("ISBNで登録")
        }
    }
}

@Preview
@Composable
fun PreviewRegistProcessScreen(){
    RegisterProcessScreen()
}