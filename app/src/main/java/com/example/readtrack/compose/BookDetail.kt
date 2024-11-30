package com.example.readtrack.compose

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.readtrack.ReadTrackApplication
import com.example.readtrack.network.BookData
import com.example.readtrack.network.BookViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun BookDetail(
    navController: NavController,
    bookViewModel: BookViewModel,
) {
    val bookItem by bookViewModel.book
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val app = context.applicationContext as ReadTrackApplication
    val db = app.appContainer.booksRepository
    val currentDateTime = LocalDateTime.now()
    val formattedDate = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm"))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            AsyncImage(
                model = bookItem.volumeInfo.imageLinks.thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(16.dp)
            )
            Column {
                Text(
                    text = bookItem.volumeInfo.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = bookItem.volumeInfo.authors.toString(),
                )
                Text(
                    text = bookItem.volumeInfo.publishedDate.toString(),
                )
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            //あらすじについてはボタンを押すとすべてを表示するように後で修正する？
            if (bookItem.volumeInfo.description.toString().length > 50) {
                Text(text = bookItem.volumeInfo.description.toString().substring(0, 50) + "...")
            } else {
                Text(text = bookItem.volumeInfo.description.toString())
            }

        }
        //このRowでは本に関する追加情報を表示する
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "ジャンル")
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp)
                )
                if (bookItem.volumeInfo.categories?.get(0).toString().length > 12) {
                    Text(
                        text = bookItem.volumeInfo.categories?.get(0).toString()
                            .substring(0, 12) + "..."
                    )
                } else {
                    Text(text = bookItem.volumeInfo.categories?.get(0) ?: "ジャンルなし")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "ページ数")
                Icon(
                    imageVector = Icons.Filled.Book,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp)
                )
                Text(text = bookItem.volumeInfo.pageCount.toString() + "ページ")
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "出版社")
                Icon(
                    imageVector = Icons.Filled.Business,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp)
                )
                if (bookItem.volumeInfo.publisher.toString().length > 5) {
                    Text(text = bookItem.volumeInfo.publisher.toString().substring(0, 5) + "...")
                } else {
                    Text(text = bookItem.volumeInfo.publisher.toString())
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        Button(
            onClick = {
                val book = BookData(
                    id = bookItem.id,
                    title = bookItem.volumeInfo.title,
                    author = bookItem.volumeInfo.authors?.get(0) ?: "",
                    publisher = bookItem.volumeInfo.publisher,
                    publishedDate = bookItem.volumeInfo.publishedDate,
                    description = bookItem.volumeInfo.description,
                    thumbnail = bookItem.volumeInfo.imageLinks.thumbnail,
                    pageCount = bookItem.volumeInfo.pageCount,
                    registeredDate = formattedDate,
                    updatedDate = formattedDate
                )
                coroutineScope.launch {
                    db.insert(book)
                }
                Toast.makeText(context, "ライブラリに追加しました", Toast.LENGTH_SHORT).show()
            },
        ) {
            Text("ライブラリに追加")
        }
    }
}
