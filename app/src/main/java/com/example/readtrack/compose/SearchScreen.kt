package com.example.readtrack.compose

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import coil.compose.AsyncImage
import com.example.readtrack.BuildConfig
import com.example.readtrack.R
import com.example.readtrack.ReadTrackScreen
import com.example.readtrack.network.BookItem
import com.example.readtrack.network.BookListViewModel

@Composable
fun SearchScreen(
    viewModel: BookListViewModel,
    navController: NavController
) {
    var query by remember { mutableStateOf("Kotlin") }
    val apiKey= BuildConfig.API_KEY
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search Books") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { viewModel.searchBooks(query, apiKey) }) {
            Text("Search")
        }
        Spacer(modifier = Modifier.height(16.dp))

        when {
            viewModel.isLoading -> CircularProgressIndicator()
            viewModel.errorMessage != null -> Text("Error: ${viewModel.errorMessage}")
            else -> BooksList(
                books = viewModel.books,
                navController = navController
            )
        }
    }
}


@Composable
fun BooksList(
    books: List<BookItem>,
    navController: NavController
) {
    LazyColumn {
        itemsIndexed(books) { index,book ->
            BookCard(book, navController,index)
            HorizontalDivider()
        }
    }
}


@Composable
fun BookCard(
    book:BookItem,
    navController: NavController,
    index:Int
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {

                navController.navigate(ReadTrackScreen.BookDetail.name)
            }
    ) {
        Column {
            AsyncImage(
                model = book.volumeInfo.imageLinks.thumbnail,
                contentDescription = null,
            )
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

