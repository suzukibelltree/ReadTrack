package com.example.readtrack.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.readtrack.ReadTrackScreen
import com.example.readtrack.network.BookData
import com.example.readtrack.room.SavedBooksViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    savedBooksViewModel: SavedBooksViewModel
) {
    val savedBooks = savedBooksViewModel.savedBooks.collectAsState()
    val finishedBooks = savedBooks.value.filter { it.progress == 2 }
    // もっとも最近に更新された本のインスタンスを取得
    val latestBook = savedBooks.value.maxByOrNull { it.updatedDate }
    val newBook = savedBooks.value.maxByOrNull { it.registeredDate }
    Column {
        Text(
            text = "これまでに読了した本：${finishedBooks.size}冊",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
        Text(
            text ="最後に更新された本",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
        latestBook?.let {
            MiniBookCard(
                it,
                navController,
                "${latestBook.updatedDate}にアクセスしました")
        }
        Text(
            text = "新しく登録された本",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
        newBook?.let {
            MiniBookCard(
                it,
                navController,
                "${newBook.registeredDate}に追加されました")
        }
    }
}

@Composable
fun MiniBookCard(
    book: BookData,
    navController: NavController,
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        onClick = { navController.navigate("${ReadTrackScreen.MyBook.name}/${book.id}") }
    ) {
        Row {
            AsyncImage(
                model = book.thumbnail,
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Column {
                Text(book.title)
                Text(text = message)
            }
        }
    }
}
