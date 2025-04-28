package com.belltree.readtrack.compose.search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belltree.readtrack.network.BookItem
import com.belltree.readtrack.room.BooksRepository
import com.belltree.readtrack.utils.ConvertBookItemToBookData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchedBookDetailViewModel @Inject constructor(
    private val repository: BooksRepository
) : ViewModel() {

    private val _isRegistered = mutableStateOf(false)
    val isRegistered: State<Boolean> = _isRegistered

    private val _bookItem = mutableStateOf<BookItem?>(null)
    val bookItem: State<BookItem?> = _bookItem

    fun loadBookById(bookId: String, sourceBookItem: BookItem?) {
        viewModelScope.launch {
            // 書籍 ID の一覧を取得して登録済みか判定
            val ids = repository.getAllBookIds()
            _isRegistered.value = bookId in ids
            
            // bookItem（API で取得したデータ）をセット
            _bookItem.value = sourceBookItem
        }
    }


    fun addBook(bookItem: BookItem) {
        viewModelScope.launch {
            val book = ConvertBookItemToBookData(bookItem)
            repository.insert(book)
        }
    }
}