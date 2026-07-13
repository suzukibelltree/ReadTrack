package com.belltree.readtrack.ui.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belltree.readtrack.R
import com.belltree.readtrack.domain.usecase.GetHomeStaticsUseCase
import com.belltree.readtrack.ui.home.HomeBindingModelConverter.convertToHomeBookBindingModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val bindingModel: HomeBindingModel
    ) : HomeUiState

    data class Error(@StringRes val messageResId: Int) : HomeUiState
}

/**
 * ホーム画面に対応したViewModel
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeStaticsUseCase: GetHomeStaticsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState


    init {
        loadHomeData()
    }

    /**
     * エラー表示からの再読み込み
     */
    fun retry() {
        _uiState.value = HomeUiState.Loading
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            runCatching {
                getHomeStaticsUseCase()
            }.onSuccess { summary ->
                val bindingModel = HomeBindingModel(
                    numOfReadBooks = summary.numOfReadBooks,
                    newlyAddedBook = convertToHomeBookBindingModel(summary.newlyAddedBookData),
                    recentlyReadBook = convertToHomeBookBindingModel(summary.recentlyReadBookData),
                    readLogForGraph = summary.recentReadLogs
                )
                _uiState.value = HomeUiState.Success(bindingModel)
            }.onFailure {
                _uiState.value = HomeUiState.Error(R.string.home_error_load)
            }
        }
    }
}