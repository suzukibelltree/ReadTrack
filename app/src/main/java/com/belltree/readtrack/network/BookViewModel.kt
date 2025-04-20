package com.belltree.readtrack.network

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.belltree.readtrack.BuildConfig
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
 * 検索画面で使用する
 * @param apiService Google Books APIを利用するためのインターフェース
 */
@HiltViewModel
class BookListViewModel @Inject constructor(private val apiService: GoogleBooksApiService) :
    ViewModel() {
    private val _books = mutableStateListOf<BookItem>()
    val books: List<BookItem> get() = _books

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val bookPagingData: Flow<PagingData<BookItem>> = query
        .filter { it.isNotBlank() }
        .distinctUntilChanged() // 同じクエリなら再検索しない
        .flatMapLatest { query ->
            Pager(PagingConfig(pageSize = 10)) {
                BookPagingSource(
                    bookApiService = apiService,
                    query = query,
                    apiKey = BuildConfig.API_KEY
                )
            }.flow
        }
        .cachedIn(viewModelScope)

    // 本のIDから本を取得する
    fun fetchBookById(bookId: String): BookItem? {
        return _books.find { it.id == bookId }
    }

    // 本の検索結果を破棄する
    fun clearSearchResults() {
        _books.clear()
        isLoading = false
        errorMessage = null
    }

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }
}

