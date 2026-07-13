package com.belltree.readtrack

import app.cash.turbine.test
import com.belltree.readtrack.domain.model.BookData
import com.belltree.readtrack.domain.model.ReadLogByMonth
import com.belltree.readtrack.domain.usecase.GetHomeStaticsUseCase
import com.belltree.readtrack.domain.usecase.HomeStatics
import com.belltree.readtrack.ui.home.HomeUiState
import com.belltree.readtrack.ui.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getHomeStaticsUseCase: GetHomeStaticsUseCase = mockk()

    private fun buildHomeStatics(
        numOfReadBooks: Int = 0,
        newlyAddedBookData: BookData? = null,
        recentlyReadBookData: BookData? = null,
        recentReadLogs: List<ReadLogByMonth> = emptyList()
    ) = HomeStatics(
        numOfReadBooks = numOfReadBooks,
        newlyAddedBookData = newlyAddedBookData,
        recentlyReadBookData = recentlyReadBookData,
        recentReadLogs = recentReadLogs
    )

    private fun buildBook(id: String = "book-1", title: String = "Test Book") = BookData(
        id = id,
        title = title,
        author = "Author",
        publisher = null,
        publishedDate = null,
        description = null,
        thumbnail = "https://thumbnail.url",
        pageCount = null,
        registeredDate = "2025/01/01/10:00",
        updatedDate = "2025/01/02/10:00"
    )

    @Test
    fun `initial state is Loading`() = runTest {
        coEvery { getHomeStaticsUseCase() } returns buildHomeStatics()
        val viewModel = HomeViewModel(getHomeStaticsUseCase)

        viewModel.uiState.test {
            assertTrue(awaitItem() is HomeUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Success with correct numOfReadBooks when use case succeeds`() = runTest {
        coEvery { getHomeStaticsUseCase() } returns buildHomeStatics(numOfReadBooks = 3)
        val viewModel = HomeViewModel(getHomeStaticsUseCase)

        viewModel.uiState.test {
            awaitItem() // Loading
            val state = awaitItem() as HomeUiState.Success
            assertEquals(3, state.bindingModel.numOfReadBooks)
        }
    }

    @Test
    fun `emits Success with null book fields when use case returns no books`() = runTest {
        coEvery { getHomeStaticsUseCase() } returns buildHomeStatics()
        val viewModel = HomeViewModel(getHomeStaticsUseCase)

        viewModel.uiState.test {
            awaitItem() // Loading
            val state = awaitItem() as HomeUiState.Success
            assertNull(state.bindingModel.newlyAddedBook)
            assertNull(state.bindingModel.recentlyReadBook)
        }
    }

    @Test
    fun `maps newlyAddedBookData to HomeBookBindingModel in Success state`() = runTest {
        val book = buildBook(id = "book-1", title = "Kotlin Book")
        coEvery { getHomeStaticsUseCase() } returns buildHomeStatics(newlyAddedBookData = book)
        val viewModel = HomeViewModel(getHomeStaticsUseCase)

        viewModel.uiState.test {
            awaitItem() // Loading
            val state = awaitItem() as HomeUiState.Success
            assertEquals("book-1", state.bindingModel.newlyAddedBook?.id)
            assertEquals("Kotlin Book", state.bindingModel.newlyAddedBook?.title)
            assertEquals("https://thumbnail.url", state.bindingModel.newlyAddedBook?.thumbnail)
        }
    }

    @Test
    fun `maps recentlyReadBookData to HomeBookBindingModel in Success state`() = runTest {
        val book = buildBook(id = "book-2", title = "Recently Read")
        coEvery { getHomeStaticsUseCase() } returns buildHomeStatics(recentlyReadBookData = book)
        val viewModel = HomeViewModel(getHomeStaticsUseCase)

        viewModel.uiState.test {
            awaitItem() // Loading
            val state = awaitItem() as HomeUiState.Success
            assertEquals("book-2", state.bindingModel.recentlyReadBook?.id)
            assertEquals("Recently Read", state.bindingModel.recentlyReadBook?.title)
        }
    }

    @Test
    fun `passes readLogForGraph through to binding model in Success state`() = runTest {
        val logs = listOf(
            ReadLogByMonth(yearMonthId = 202501, totalReadPages = 100),
            ReadLogByMonth(yearMonthId = 202502, totalReadPages = 200),
        )
        coEvery { getHomeStaticsUseCase() } returns buildHomeStatics(recentReadLogs = logs)
        val viewModel = HomeViewModel(getHomeStaticsUseCase)

        viewModel.uiState.test {
            awaitItem() // Loading
            val state = awaitItem() as HomeUiState.Success
            assertEquals(logs, state.bindingModel.readLogForGraph)
        }
    }

    @Test
    fun `emits Error with message resource id when use case throws`() = runTest {
        coEvery { getHomeStaticsUseCase() } throws RuntimeException("network error")
        val viewModel = HomeViewModel(getHomeStaticsUseCase)

        viewModel.uiState.test {
            awaitItem() // Loading
            val state = awaitItem() as HomeUiState.Error
            assertEquals(R.string.home_error_load, state.messageResId)
        }
    }

    @Test
    fun `retry emits Loading then Success after error when use case recovers`() = runTest {
        coEvery { getHomeStaticsUseCase() } throws RuntimeException("network error")
        val viewModel = HomeViewModel(getHomeStaticsUseCase)

        viewModel.uiState.test {
            awaitItem() // Loading
            assertTrue(awaitItem() is HomeUiState.Error)

            coEvery { getHomeStaticsUseCase() } returns buildHomeStatics(numOfReadBooks = 2)
            viewModel.retry()

            assertTrue(awaitItem() is HomeUiState.Loading)
            val state = awaitItem() as HomeUiState.Success
            assertEquals(2, state.bindingModel.numOfReadBooks)
        }
    }
}