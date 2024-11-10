package com.example.readtrack.network

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BookListViewModel : ViewModel() {
    private val _books = mutableStateListOf<BookItem>()
    val books: List<BookItem> get() = _books

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun getBookItemById(bookId: String): BookItem? {
        return books.find { it.id == bookId }
    }

    fun searchBooks(query: String, apiKey: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitInstance.api.searchBooks(query, apiKey)
                _books.clear()
                response.items?.let { _books.addAll(it) }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}

class BookViewModelFactory(private val bookItem: BookItem) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookViewModel(bookItem) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class BookViewModel(
    bookItem: BookItem
) :ViewModel(){
    val book= mutableStateOf(bookItem)
    val comment = mutableStateOf<String?>(null)
    val progress = mutableStateOf<Int?>(null)
}

