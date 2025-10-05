package com.belltree.readtrack.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belltree.readtrack.domain.usecase.GetAllBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
 * @param getAllBooksUseCase すべての書籍情報を取得するユースケース
 */
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getAllBooksUseCase: GetAllBooksUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState

    init {
        loadLibraryData()
    }

    private fun loadLibraryData() {
        viewModelScope.launch {
            val savedBooks = getAllBooksUseCase()
            val newBindingModel = LibraryBindingModelConverter.convertToLibraryBindingModel(
                savedBooks.map { LibraryBindingModelConverter.convertToLibraryBookBindingModel(it) }
            )
            _uiState.value = LibraryUiState.Success(newBindingModel)
        }
    }
}