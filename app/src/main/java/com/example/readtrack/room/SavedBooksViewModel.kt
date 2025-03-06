package com.example.readtrack.room


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readtrack.network.BookData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 登録された本のリストの情報を保持するViewModel
 * ライブラリ画面とそこから遷移するMyBookScreenで使用する
 * @param booksRepository 本の情報を取得するためのリポジトリ
 * @param readLogRepository 読書ログの情報を取得するためのリポジトリ
 */
@HiltViewModel
class SavedBooksViewModel @Inject constructor(
    private val booksRepository: BooksRepository,
    private val readLogRepository: ReadLogRepository
) : ViewModel() {
    // ローカルに保存されている本の情報
    private val _savedBooks = MutableStateFlow<BookData?>(null)
    val savedBooks: StateFlow<List<BookData>> = booksRepository.allBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _selectedBook = MutableStateFlow<BookData?>(null)
    val selectedBook: StateFlow<BookData?> = _selectedBook

    // ローカルに保存されている読書ログの情報
    private val _allLogs = MutableStateFlow<ReadLog?>(null)
    val allLogs: StateFlow<List<ReadLog>> = readLogRepository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

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

    /**
     * 読書ログの更新
     */
    fun upsertLog(readLog: ReadLog) {
        viewModelScope.launch {
            readLogRepository.upsertLog(readLog)
        }
    }
}