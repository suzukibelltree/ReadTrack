package com.example.readtrack.room


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readtrack.network.BookData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


/**
 * 登録された本のリストの情報を保持するViewModel
 * @param booksRepository 本の情報を取得するためのリポジトリ
 */
class SavedBooksViewModel(private val booksRepository: BooksRepository) : ViewModel() {
    val savedBooks: StateFlow<List<BookData>> = booksRepository.getAllBooksFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _selectedBook = MutableStateFlow<BookData?>(null)
    val selectedBook: StateFlow<BookData?> = _selectedBook

    /**
     * IDから本の情報を取得する
     * @param savedBookId 本のID
     */
    fun selectBook(savedBookId: String) {
        _selectedBook.value = savedBooks.value.find { it.id == savedBookId }
    }

    /**
     * 本の情報を取得する
     */
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

    /**
     * 本の情報を更新する
     * @param book 本の情報
     */
    fun updateBook(book: BookData) {
        viewModelScope.launch {
            booksRepository.updateBook(book)
        }
    }

    /**
     * 本の情報を削除する
     * @param book 本の情報
     */
    fun deleteBook(book: BookData) {
        viewModelScope.launch {
            booksRepository.deleteBook(book)
        }
    }
}