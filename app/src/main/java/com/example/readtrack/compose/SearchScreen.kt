package com.example.readtrack.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.readtrack.network.BookItem
import com.example.readtrack.network.BooksViewModel

@Composable
fun SearchScreen(viewModel: BooksViewModel) {
    var query by remember { mutableStateOf("Kotlin") }
    val apiKey="AIzaSyDQFvHINYsbSaR_PsbRYohz7ClqwuKIJ-w"
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
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
            else -> BooksList(books = viewModel.books)
        }
    }
}


@Composable
fun BooksList(books: List<BookItem>) {
    LazyColumn {
        items(books) { book ->
            BookItemView(book)
            Divider()
        }
    }
}

@Composable
fun BookItemView(book: BookItem) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = book.volumeInfo.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Author(s): ${book.volumeInfo.authors?.joinToString(", ") ?: "Unknown"}",
        )
        Text(
            text = book.volumeInfo.description ?: "No description",
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}