package com.belltree.readtrack.ui.search.title

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.belltree.readtrack.data.repository.BooksRemoteRepository
import com.belltree.readtrack.domain.model.BookItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * Google Books APIから取得した本の情報を保持するViewModel
 * タイトル検索画面で使用する
 * @param repository Google Books APIから本の情報を取得するリポジトリ
 */
@HiltViewModel
class TitleSearchViewModel @Inject constructor(
    private val repository: BooksRemoteRepository
) :
    ViewModel() {

    private val _selectedBookItem = MutableStateFlow<BookItem?>(null)
    val selectedBookItem: StateFlow<BookItem?> = _selectedBookItem

    private val _books = mutableListOf<BookItem>()
    val books: List<BookItem> = _books

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val bookPagingData: Flow<PagingData<BookItem>> = query
        .filter { it.isNotBlank() }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            Pager(PagingConfig(pageSize = 10)) {
                repository.getBookPagingSource(query)
            }.flow
        }
        .cachedIn(viewModelScope)


    // 本の検索結果を破棄する
    fun clearSearchResults() {
        _books.clear()
        isLoading = false
        errorMessage = null
    }

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }


    fun selectBookItem(bookItem: BookItem) {
        _selectedBookItem.value = bookItem
    }
}