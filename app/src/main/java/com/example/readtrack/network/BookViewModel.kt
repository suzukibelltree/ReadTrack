package com.example.readtrack.network

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * Google Books APIから取得した本の情報を保持するViewModel
 */
class BookListViewModel : ViewModel() {
    private val _books = mutableStateListOf<BookItem>()
    val books: List<BookItem> get() = _books

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Google Books APIから本を検索する
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

    // 本のIDから本を取得する
    fun fetchBookById(bookId: String): BookItem? {
        return _books.find { it.id == bookId }
    }
}

