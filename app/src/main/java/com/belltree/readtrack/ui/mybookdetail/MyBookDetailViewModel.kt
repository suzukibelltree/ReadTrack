package com.belltree.readtrack.ui.mybookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belltree.readtrack.domain.model.BookData
import com.belltree.readtrack.domain.model.ReadLog
import com.belltree.readtrack.domain.repository.BooksRepository
import com.belltree.readtrack.domain.repository.ReadLogRepository
import com.belltree.readtrack.domain.usecase.DeleteBookUseCase
import com.belltree.readtrack.domain.usecase.InsertReadLogUseCase
import com.belltree.readtrack.domain.usecase.UpdateBookUseCase
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

sealed interface MyBookDetailUiState {
    data object Loading : MyBookDetailUiState
    data class Success(val bindingModel: MyBookDetailBindingModel) : MyBookDetailUiState
    data class Error(val message: String) : MyBookDetailUiState
}

@HiltViewModel
class MyBookDetailViewModel @Inject constructor(
    private val booksRepository: BooksRepository,
    private val readLogRepository: ReadLogRepository,
    private val updateBookUseCase: UpdateBookUseCase,
    private val insertReadLogUseCase: InsertReadLogUseCase,
    private val deleteBookUseCase: DeleteBookUseCase
) : ViewModel() {

    // --- UI状態制御（ダイアログなど） ---
    private val _showCompleteDialog = MutableStateFlow(false)
    val showCompleteDialog: StateFlow<Boolean> = _showCompleteDialog

    fun openCompleteDialog() {
        _showCompleteDialog.value = true
    }

    fun closeCompleteDialog() {
        _showCompleteDialog.value = false
    }

    private val _bookId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val selectedBookFlow: Flow<BookData?> =
        _bookId.filterNotNull().flatMapLatest { bookId ->
            booksRepository.getBookByIdFlow(bookId)
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
        when (book) {
            null -> MyBookDetailUiState.Loading
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
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MyBookDetailUiState.Loading
    )

    fun setBookId(bookId: String) {
        _bookId.value = bookId
    }

    fun updateBook(progress: Int, readPages: Int, comment: String?, updatedDate: String) {
        viewModelScope.launch {
            _bookId.value?.let { bookId ->
                updateBookUseCase(bookId, progress, readPages, comment, updatedDate)
            }
        }
    }

    fun insertLog(readLog: ReadLog) {
        viewModelScope.launch {
            insertReadLogUseCase(readLog)
        }
    }

    fun deleteBook() {
        viewModelScope.launch {
            _bookId.value?.let { bookId ->
                deleteBookUseCase(bookId)
            }
        }
    }
}
