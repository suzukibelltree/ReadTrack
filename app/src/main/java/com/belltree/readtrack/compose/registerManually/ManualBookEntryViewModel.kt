package com.belltree.readtrack.compose.registerManually

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belltree.readtrack.network.BookData
import com.belltree.readtrack.room.BooksRepository
import com.belltree.readtrack.utils.getCurrentFormattedTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * 手動で書籍情報を登録するためのUiState
 */
data class ManualBookFormState(
    val title: String = "",
    val author: String = "",
    val publisher: String = "",
    val publishedDate: String = "",
    val pageCount: String = "",
    val thumbnail: String? = null
) {
    val isSaveEnabled: Boolean get() = title.isNotBlank()
}

/**
 * 手動で書籍情報を登録するためのViewModel
 * @param booksRepository 書籍情報を保存するRepository
 */
@HiltViewModel
class ManualBookEntryViewModel @Inject constructor(
    private val booksRepository: BooksRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(ManualBookFormState())
    val formState: StateFlow<ManualBookFormState> = _formState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ManualBookUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun updateTitle(title: String) {
        _formState.update { it.copy(title = title) }
    }

    fun updateAuthor(author: String) {
        _formState.update { it.copy(author = author) }
    }

    fun updatePublisher(publisher: String) {
        _formState.update { it.copy(publisher = publisher) }
    }

    fun updatePublishedDate(date: String) {
        _formState.update { it.copy(publishedDate = date) }
    }

    fun updatePageCount(count: String) {
        _formState.update { it.copy(pageCount = count) }
    }

    fun onThumbnailSelected(thumbnail: String?) {
        _formState.update { it.copy(thumbnail = thumbnail) }
    }

    fun onThumbnailSelectionCanceled() {
        viewModelScope.launch {
            _eventFlow.emit(ManualBookUiEvent.ThumbnailSelectionCanceled)
        }
    }

    fun onCameraPermissionDenied() {
        viewModelScope.launch {
            _eventFlow.emit(ManualBookUiEvent.CameraPermissionDenied)
        }
    }

    /**
     * 書籍情報を保存する
     */
    fun saveBook(onSaved: () -> Unit) {
        val state = _formState.value
        if (!state.isSaveEnabled) return

        viewModelScope.launch {
            val id = UUID.randomUUID().toString()

            val book = BookData(
                id = id,
                title = state.title.trim(),
                author = state.author.trim(),
                publisher = state.publisher.takeIf { it.isNotBlank() },
                publishedDate = state.publishedDate.takeIf { it.isNotBlank() },
                description = null,
                thumbnail = state.thumbnail,
                pageCount = state.pageCount.toIntOrNull() ?: 0,
                registeredDate = getCurrentFormattedTime(),
                updatedDate = getCurrentFormattedTime()
            )

            booksRepository.insert(book)
            _eventFlow.emit(ManualBookUiEvent.BookSaved)
            onSaved()
        }
    }
}


