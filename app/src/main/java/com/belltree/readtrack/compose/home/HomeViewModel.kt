package com.belltree.readtrack.compose.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.belltree.readtrack.network.BookData
import com.belltree.readtrack.room.BooksRepository
import com.belltree.readtrack.room.ReadLog
import com.belltree.readtrack.room.ReadLogByMonth
import com.belltree.readtrack.room.ReadLogRepository
import com.belltree.readtrack.utils.getRecentFourMonthsAsIntList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ホーム画面に対応したViewModel
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    savedBooksRepository: BooksRepository,
    readLogRepository: ReadLogRepository
) : ViewModel() {
    // ローカルに保存されている本の情報
    val allBooks: StateFlow<List<BookData>> = savedBooksRepository.allBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val recent4MonthsId: List<Int> = getRecentFourMonthsAsIntList()

    private val recent4ReadLogs: Flow<List<ReadLog>> =
        readLogRepository.getReadLogsForMonths(recent4MonthsId)

    val recentMonthlySummary: Flow<List<ReadLogByMonth>> =
        recent4ReadLogs.map { logs ->
            logs.groupBy { it.yearMonthId }
                .map { (yearMonthId, logsForMonth) ->
                    ReadLogByMonth(
                        yearMonthId = yearMonthId,
                        totalReadPages = logsForMonth.sumOf { it.readPages }
                    )
                }
                .sortedBy { it.yearMonthId }
        }

}