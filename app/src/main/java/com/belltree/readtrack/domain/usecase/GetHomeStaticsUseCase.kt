package com.belltree.readtrack.domain.usecase

import com.belltree.readtrack.data.repository.DatabaseBooksRepository
import com.belltree.readtrack.data.repository.DatabaseReadLogRepository
import com.belltree.readtrack.domain.model.BookData
import com.belltree.readtrack.domain.model.ReadLogByMonth
import com.belltree.readtrack.ui.mybookdetail.ReadProgress
import javax.inject.Inject

data class HomeStatics(
    val numOfReadBooks: Int,
    val newlyAddedBookData: BookData?,
    val recentlyReadBookData: BookData?,
    val recentReadLogs: List<ReadLogByMonth>
)

class GetHomeStaticsUseCase @Inject constructor(
    private val savedBooksRepository: DatabaseBooksRepository,
    private val readLogRepository: DatabaseReadLogRepository
) {
    suspend operator fun invoke(): HomeStatics {
        val books = savedBooksRepository.getAllBooks()
        val numOfReadBooks = books.count { it.progress == ReadProgress.READ }
        val newlyAddedBook = books.maxByOrNull { it.registeredDate }
        val recentlyReadBook = books.maxByOrNull { it.updatedDate }

        val recentReadLogsId = com.belltree.readtrack.core.getRecentFourMonthsAsIntList()
        val recentReadLogs = readLogRepository.getReadLogsForMonths(recentReadLogsId)
            .groupBy { it.yearMonthId }
            .map { (yearMonthId, logsForMonth) ->
                ReadLogByMonth(
                    yearMonthId = yearMonthId,
                    totalReadPages = logsForMonth.sumOf { it.readPages }
                )
            }
            .sortedBy { it.yearMonthId }

        return HomeStatics(
            numOfReadBooks = numOfReadBooks,
            newlyAddedBookData = newlyAddedBook,
            recentlyReadBookData = recentlyReadBook,
            recentReadLogs = recentReadLogs
        )
    }
}