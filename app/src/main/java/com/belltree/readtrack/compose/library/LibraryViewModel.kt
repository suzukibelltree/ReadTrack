package com.belltree.readtrack.compose.library


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belltree.readtrack.network.BookData
import com.belltree.readtrack.room.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


/**
 * 登録された本のリストの情報を保持するViewModel
 * ライブラリ画面とそこから遷移するMyBookScreenで使用する
 * @param booksRepository 本の情報を取得するためのリポジトリ
 */
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val booksRepository: BooksRepository,
) : ViewModel() {
    // ローカルに保存されている本の情報
    val savedBooks: StateFlow<List<BookData>> = booksRepository.allBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
}