package com.belltree.readtrack.ui.search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belltree.readtrack.data.mapper.ConvertBookItemToBookData
import com.belltree.readtrack.data.repository.BooksRemoteRepository
import com.belltree.readtrack.domain.model.BookItem
import com.belltree.readtrack.domain.repository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchedBookDetailViewModel @Inject constructor(
    private val repository: BooksRepository,
    private val booksRemoteRepository: BooksRemoteRepository
) : ViewModel() {

    private val _isRegistered = mutableStateOf(false)
    val isRegistered: State<Boolean> = _isRegistered

    private val _bookItem = MutableStateFlow<BookItem?>(null)
    val bookItem: StateFlow<BookItem?> = _bookItem

    fun loadBookById(bookId: String) {
        viewModelScope.launch {
            // 書籍 ID の一覧を取得して登録済みか判定
            val ids = repository.getAllBookIds()
            _isRegistered.value = bookId in ids
            _bookItem.value = booksRemoteRepository.getBookById(bookId)
        }
    }


    fun addBook(bookItem: BookItem) {
        viewModelScope.launch {
            val book = ConvertBookItemToBookData(bookItem)
            repository.insert(book)
        }
    }
}