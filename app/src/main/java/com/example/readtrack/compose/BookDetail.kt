package com.example.readtrack.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.readtrack.network.BookViewModel

@Composable
fun BookDetail(
    navController: NavController,
    bookViewModel: BookViewModel,
){
    val bookItem by bookViewModel.book
    Text(text = bookItem.volumeInfo.title)
}