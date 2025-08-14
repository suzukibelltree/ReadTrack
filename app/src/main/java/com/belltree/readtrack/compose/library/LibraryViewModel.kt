package com.belltree.readtrack.compose.library


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belltree.readtrack.compose.library.bindingmodel.LibraryBindingModel
import com.belltree.readtrack.compose.library.bindingmodel.LibraryBindingModelConverter
import com.belltree.readtrack.network.BookData
import com.belltree.readtrack.room.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data class Success(val bindingModel: LibraryBindingModel) : LibraryUiState
    data class Error(val message: String) : LibraryUiState
}


/**
 * 登録された本のリストの情報を保持するViewModel
 * ライブラリ画面とそこから遷移するMyBookScreenで使用する
 * @param booksRepository 本の情報を取得するためのリポジトリ
 */
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val booksRepository: BooksRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState

    init {
        loadLibraryData()
    }

    private fun loadLibraryData() {
        viewModelScope.launch {
            val savedBooks = booksRepository.getAllBooks()
            val newBindingModel = LibraryBindingModelConverter.convertToLibraryBindingModel(
                savedBooks.map { LibraryBindingModelConverter.convertToLibraryBookBindingModel(it) }
            )
            _uiState.value = LibraryUiState.Success(newBindingModel)
        }
    }

    // ローカルに保存されている本の情報
    val savedBooks: StateFlow<List<BookData>> = booksRepository.allBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
}