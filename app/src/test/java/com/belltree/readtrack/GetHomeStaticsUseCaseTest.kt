package com.belltree.readtrack

import com.belltree.readtrack.domain.model.BookData
import com.belltree.readtrack.domain.model.ReadLog
import com.belltree.readtrack.domain.repository.BooksRepository
import com.belltree.readtrack.domain.repository.ReadLogRepository
import com.belltree.readtrack.domain.usecase.GetHomeStaticsUseCase
import com.belltree.readtrack.ui.mybookdetail.ReadProgress
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GetHomeStaticsUseCaseTest {

    private val booksRepository: BooksRepository = mockk()
    private val readLogRepository: ReadLogRepository = mockk()
    private val useCase = GetHomeStaticsUseCase(booksRepository, readLogRepository)

    private fun buildBook(
        id: String,
        progress: Int = ReadProgress.UNREAD,
        registeredDate: String = "",
        updatedDate: String = ""
    ) = BookData(
        id = id,
        title = "Title $id",
        author = "Author",
        publisher = null,
        publishedDate = null,
        description = null,
        thumbnail = null,
        pageCount = null,
        progress = progress,
        registeredDate = registeredDate,
        updatedDate = updatedDate
    )

    private fun buildLog(logId: Int, readPages: Int, yearMonthId: Int) = ReadLog(
        logId = logId,
        bookId = "book-$logId",
        readPages = readPages,
        recordedAt = "",
        yearMonthId = yearMonthId
    )

    @Test
    fun `returns zero numOfReadBooks and nulls and empty list when no books exist`() = runTest {
        coEvery { booksRepository.getAllBooks() } returns emptyList()
        coEvery { readLogRepository.getReadLogsForMonths(any()) } returns emptyList()

        val result = useCase()

        assertEquals(0, result.numOfReadBooks)
        assertNull(result.newlyAddedBookData)
        assertNull(result.recentlyReadBookData)
        assertEquals(emptyList<Any>(), result.recentReadLogs)
    }

    @Test
    fun `counts only READ books in numOfReadBooks`() = runTest {
        val books = listOf(
            buildBook("1", progress = ReadProgress.UNREAD),
            buildBook("2", progress = ReadProgress.READING),
            buildBook("3", progress = ReadProgress.READ),
            buildBook("4", progress = ReadProgress.READ),
        )
        coEvery { booksRepository.getAllBooks() } returns books
        coEvery { readLogRepository.getReadLogsForMonths(any()) } returns emptyList()

        val result = useCase()

        assertEquals(2, result.numOfReadBooks)
    }

    @Test
    fun `returns book with latest registeredDate as newlyAddedBookData`() = runTest {
        val books = listOf(
            buildBook("1", registeredDate = "2025/01/01/10:00"),
            buildBook("2", registeredDate = "2025/03/01/10:00"),
            buildBook("3", registeredDate = "2025/02/01/10:00"),
        )
        coEvery { booksRepository.getAllBooks() } returns books
        coEvery { readLogRepository.getReadLogsForMonths(any()) } returns emptyList()

        val result = useCase()

        assertEquals("2", result.newlyAddedBookData?.id)
    }

    @Test
    fun `returns book with latest updatedDate as recentlyReadBookData`() = runTest {
        val books = listOf(
            buildBook("1", updatedDate = "2025/01/01/10:00"),
            buildBook("2", updatedDate = "2025/03/01/10:00"),
            buildBook("3", updatedDate = "2025/02/01/10:00"),
        )
        coEvery { booksRepository.getAllBooks() } returns books
        coEvery { readLogRepository.getReadLogsForMonths(any()) } returns emptyList()

        val result = useCase()

        assertEquals("2", result.recentlyReadBookData?.id)
    }

    @Test
    fun `sums readPages of multiple logs in the same month into one ReadLogByMonth`() = runTest {
        val logs = listOf(
            buildLog(logId = 1, readPages = 50, yearMonthId = 202501),
            buildLog(logId = 2, readPages = 30, yearMonthId = 202501),
        )
        coEvery { booksRepository.getAllBooks() } returns emptyList()
        coEvery { readLogRepository.getReadLogsForMonths(any()) } returns logs

        val result = useCase()

        assertEquals(1, result.recentReadLogs.size)
        assertEquals(202501, result.recentReadLogs[0].yearMonthId)
        assertEquals(80, result.recentReadLogs[0].totalReadPages)
    }

    @Test
    fun `returns recentReadLogs sorted by yearMonthId ascending`() = runTest {
        val logs = listOf(
            buildLog(logId = 1, readPages = 10, yearMonthId = 202503),
            buildLog(logId = 2, readPages = 20, yearMonthId = 202501),
            buildLog(logId = 3, readPages = 30, yearMonthId = 202502),
        )
        coEvery { booksRepository.getAllBooks() } returns emptyList()
        coEvery { readLogRepository.getReadLogsForMonths(any()) } returns logs

        val result = useCase()

        assertEquals(listOf(202501, 202502, 202503), result.recentReadLogs.map { it.yearMonthId })
    }

    @Test
    fun `returns empty recentReadLogs when no read logs exist`() = runTest {
        coEvery { booksRepository.getAllBooks() } returns listOf(buildBook("1"))
        coEvery { readLogRepository.getReadLogsForMonths(any()) } returns emptyList()

        val result = useCase()

        assertEquals(emptyList<Any>(), result.recentReadLogs)
    }
}