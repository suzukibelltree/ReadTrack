package com.belltree.readtrack.room


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belltree.readtrack.network.BookData
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
class MyBooksViewModel @Inject constructor(
    private val booksRepository: BooksRepository,
    private val readLogRepository: ReadLogRepository
) : ViewModel() {
    // ローカルに保存されている本の情報
    val savedBooks: StateFlow<List<BookData>> = booksRepository.allBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _selectedBook = MutableStateFlow<BookData?>(null)
    val selectedBook: StateFlow<BookData?> = _selectedBook

    // ローカルに保存されている読書ログの情報
    val allLogs: StateFlow<List<ReadLog>> = readLogRepository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // 選択された本専用の読書ログ
    private val _selectedBookLogs = MutableStateFlow<List<ReadLog>>(emptyList())
    val selectedBookLogs: StateFlow<List<ReadLog>> = _selectedBookLogs


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
     * 特定のbookIdを持つ本の読書ログを取得する
     * @param bookId 本のID
     */
    fun getLogByBookId(bookId: String) {
        viewModelScope.launch {
            _selectedBookLogs.value = readLogRepository.getLogByBookId(bookId)
        }
    }

    /**
     * 読書ログの保存
     * @param readLog 読書ログ
     */
    fun insertLog(readLog: ReadLog) {
        viewModelScope.launch {
            readLogRepository.insertLog(readLog)
        }
    }
}