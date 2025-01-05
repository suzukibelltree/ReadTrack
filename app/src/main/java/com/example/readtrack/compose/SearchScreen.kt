package com.example.readtrack.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.readtrack.BuildConfig
import com.example.readtrack.Route
import com.example.readtrack.network.BookItem
import com.example.readtrack.network.BookListViewModel
import com.example.readtrack.network.BookViewModel
import com.example.readtrack.network.BookViewModelFactory

/**
 * 本を検索する画面
 * @param viewModel 本のリストのViewModel
 * @param navController ナビゲーションコントローラー
 */
@Composable
fun SearchScreen(
    viewModel: BookListViewModel,
    navController: NavController
) {
    var query by remember { mutableStateOf("") }
    val apiKey = BuildConfig.API_KEY
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("タイトルを入力") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { viewModel.searchBooks(query, apiKey) }) {
            Text("検索")
        }
        Spacer(modifier = Modifier.height(16.dp))

        when {
            viewModel.isLoading -> CircularProgressIndicator()
            viewModel.errorMessage != null -> Text("Error: ${viewModel.errorMessage}")
            else -> BooksCardList(
                books = viewModel.books,
                navController = navController
            )
        }
    }
}

/**
 * 検索された本のリストを表示する
 * @param books 本のリスト
 * @param navController ナビゲーションコントローラー
 */
@Composable
fun BooksCardList(
    books: List<BookItem>,
    navController: NavController
) {
    LazyColumn {
        items(books) { book ->
            BookCard(book, navController)
            HorizontalDivider()
        }
    }
}

/**
 * 検索された本一冊の情報を表示するカード
 * @param book 本の情報
 * @param navController ナビゲーションコントローラー
 */
@Composable
fun BookCard(
    book: BookItem,
    navController: NavController
) {
    viewModel<BookViewModel>(
        key = book.id,
        factory = BookViewModelFactory(book)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("${Route.BookDetail}/${book.id}")
            }
    ) {
        Row {
            AsyncImage(
                model = book.volumeInfo.imageLinks.thumbnail,
                contentDescription = null,
            )
            Column {
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = book.volumeInfo.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = book.volumeInfo.authors?.joinToString(", ") ?: "Unknown",
                    )
                    Text(
                        text = book.volumeInfo.publisher ?: "No publisher",
                    )
                }
            }
        }
    }
}

