package com.example.readtrack.room


import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readtrack.network.BookData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class SavedBooksViewModel(private val booksRepository: BooksRepository) : ViewModel() {
    val savedBooks: StateFlow<List<BookData>> = booksRepository.getAllBooksFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _selectedBook = MutableStateFlow<BookData?>(null)
    val selectedBook: StateFlow<BookData?> = _selectedBook

    fun selectBook(savedbookId: String) {
        _selectedBook.value = savedBooks.value.find { it.id == savedbookId }
    }
    // In SavedBooksViewModel.kt
    fun fetchBookDetails(bookId: String) {
        viewModelScope.launch {
            try {
                val book = booksRepository.getBookById(bookId) // Fetch book from repository
                _selectedBook.value = book
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    fun updateBook(book: BookData) {
        viewModelScope.launch {
            booksRepository.updateBook(book)
        }
    }
    fun deleteBook(book: BookData) {
        viewModelScope.launch {
            booksRepository.deleteBook(book)
        }
    }
}