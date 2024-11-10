package com.example.readtrack.network

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BookListViewModel : ViewModel() {
    private val _books = mutableStateListOf<BookItem>()
    val books: List<BookItem> get() = _books

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

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

class BookViewModel :ViewModel(){
    val book= mutableStateOf<BookItem?>(null)
    val comment = mutableStateOf<String?>(null)
    val progress = mutableStateOf<Int?>(null)
}

