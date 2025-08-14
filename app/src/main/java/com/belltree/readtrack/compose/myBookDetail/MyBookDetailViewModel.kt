package com.belltree.readtrack.compose.myBookDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belltree.readtrack.compose.myBookDetail.bindingmodel.MyBookDetailBindingModel
import com.belltree.readtrack.compose.myBookDetail.bindingmodel.MyBookDetailBindingModelConverter
import com.belltree.readtrack.network.BookData
import com.belltree.readtrack.room.BooksRepository
import com.belltree.readtrack.room.ReadLog
import com.belltree.readtrack.room.ReadLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MyBookDetailViewModelのUI状態を表すsealed interface
 * Loading, Success, Errorの3つの状態を持つ
 */
sealed interface MyBookDetailUiState {
    data object Loading : MyBookDetailUiState
    data class Success(val bindingModel: MyBookDetailBindingModel) : MyBookDetailUiState
    data class Error(val message: String) : MyBookDetailUiState
}

/**
 * 特定の本の詳細情報とその読書ログを管理するViewModel
 * @param booksRepository 本の情報を取得するためのリポジトリ
 * @param readLogRepository 読書ログの情報を取得するためのリポジトリ
 */
@HiltViewModel
class MyBookDetailViewModel @Inject constructor(
    private val booksRepository: BooksRepository,
    private val readLogRepository: ReadLogRepository
) : ViewModel() {
    private val _bookId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val selectedBookFlow: Flow<BookData?> =
        _bookId.filterNotNull().flatMapLatest { bookId ->
            booksRepository.getBookByIdFlow(bookId) // ここがFlow
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val selectedBookLogsFlow: Flow<List<ReadLog>> =
        _bookId.filterNotNull().flatMapLatest { bookId ->
            readLogRepository.getAllLogsFlow()
                .map { logs -> logs.filter { it.bookId == bookId } }
        }

    val uiState: StateFlow<MyBookDetailUiState> = combine(
        selectedBookFlow,
        selectedBookLogsFlow
    ) { book, logs ->
        when {
            book == null -> MyBookDetailUiState.Loading
            else -> MyBookDetailUiState.Success(
                MyBookDetailBindingModelConverter.convertToMyBookDetailBindingModel(
                    MyBookDetailBindingModelConverter.convertToMyBookDetailBookBindingModel(
                        id = book.id,
                        title = book.title,
                        authors = book.author,
                        thumbnail = book.thumbnail,
                        progress = book.progress,
                        pageCount = book.pageCount,
                        readPages = book.readpage,
                        comment = book.comment,
                        registeredDate = book.registeredDate,
                        updatedDate = book.updatedDate
                    ),
                    logs
                )
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MyBookDetailUiState.Loading)

    fun setBookId(bookId: String) {
        _bookId.value = bookId
    }

    fun updateBook(
        progress: Int,
        readPages: Int,
        comment: String?,
        updatedDate: String
    ) {
        viewModelScope.launch {
            _bookId.value?.let { bookId ->
                val currentBook = booksRepository.getBookById(bookId) // Flow ではなく suspend で取得
                if (currentBook != null) {
                    val updatedBook = currentBook.copy(
                        progress = progress,
                        readpage = readPages,
                        comment = comment,
                        updatedDate = updatedDate
                    )
                    booksRepository.updateBook(updatedBook)
                }
            }
        }
    }


    fun insertLog(readLog: ReadLog) {
        viewModelScope.launch {
            readLogRepository.insertLog(readLog)
        }
    }

    fun deleteBook() {
        viewModelScope.launch {
            _bookId.value?.let { bookId ->
                val book = booksRepository.getBookById(bookId)
                if (book != null) {
                    booksRepository.deleteBook(book)
                }
            }
        }
    }
}