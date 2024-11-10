package com.example.readtrack.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.readtrack.network.BookItem
import com.example.readtrack.network.BookListViewModel

@Composable
fun BookDetail(
    navController: NavController,
    viewModel: BookListViewModel,
    bookId: String
){
    //メモ
    //viewmodelを軸に解決方法を考える
    //検索で取得したリストの中でのインデックス番号を取得できれば、
    //そのインデックスを使ってbookItemを受け取らなくても解決することができる
    val bookItem = viewModel.books.find { it.id ==bookId }
    //書籍の詳細画面
    if(bookItem!=null){
        Text(text = bookItem.volumeInfo.title)
    }
}