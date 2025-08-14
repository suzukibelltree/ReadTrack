package com.belltree.readtrack.compose.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belltree.readtrack.compose.home.bindingmodel.HomeBindingModel
import com.belltree.readtrack.compose.home.bindingmodel.HomeBindingModelConverter
import com.belltree.readtrack.compose.myBookDetail.ReadProgress
import com.belltree.readtrack.room.BooksRepository
import com.belltree.readtrack.room.ReadLogByMonth
import com.belltree.readtrack.room.ReadLogRepository
import com.belltree.readtrack.utils.getRecentFourMonthsAsIntList
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

    data class Error(val message: String) : HomeUiState
}

/**
 * ホーム画面に対応したViewModel
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedBooksRepository: BooksRepository,
    private val readLogRepository: ReadLogRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState


    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            val books = savedBooksRepository.getAllBooks()
            val numOfReadBooks = books.filter { it.progress == ReadProgress.READ }.size
            val newlyAddedBook = books.maxByOrNull { it.registeredDate }
            val recentlyReadBook = books.maxByOrNull { it.updatedDate }
            val recentReadLogsId = getRecentFourMonthsAsIntList()
            val recentReadLogs = readLogRepository.getReadLogsForMonths(recentReadLogsId)
                .groupBy { it.yearMonthId }
                .map { (yearMonthId, logsForMonth) ->
                    ReadLogByMonth(
                        yearMonthId = yearMonthId,
                        totalReadPages = logsForMonth.sumOf { it.readPages }
                    )
                }
                .sortedBy { it.yearMonthId }
            val newBindingModel =
                HomeBindingModelConverter.convertToHomeBindingModel(
                    numOfReadBooks = numOfReadBooks,
                    newlyAddedBookData = newlyAddedBook,
                    recentlyReadBookData = recentlyReadBook,
                    readLogsForGraph = recentReadLogs
                )
            _uiState.value = HomeUiState.Success(newBindingModel)
        }
    }
}