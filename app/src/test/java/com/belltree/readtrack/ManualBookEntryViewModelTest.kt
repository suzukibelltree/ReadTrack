package com.belltree.readtrack

import app.cash.turbine.test
import com.belltree.readtrack.domain.usecase.SaveManualBookUseCase
import com.belltree.readtrack.ui.registermanually.ManualBookEntryViewModel
import com.belltree.readtrack.ui.registermanually.ManualBookUiEvent
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ManualBookEntryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var useCase: SaveManualBookUseCase
    private lateinit var viewModel: ManualBookEntryViewModel

    @Before
    fun setup() {
        useCase = mockk(relaxed = true)
        viewModel = ManualBookEntryViewModel(useCase)
    }

    @Test
    fun updateTitle_updatesUiState() = runTest {
        viewModel.updateTitle("Kotlin入門")

        val state = viewModel.formState.value
        assertEquals("Kotlin入門", state.title)
    }

    @Test
    fun updateAuthor_updatesUiState() = runTest {
        viewModel.updateAuthor("JetBrains")

        val state = viewModel.formState.value
        assertEquals("JetBrains", state.author)
    }

    @Test
    fun saveBook_withBlankTitle_doesNotCallUseCase() = runTest {
        var onSavedCalled = false

        viewModel.saveBook { onSavedCalled = true }
        advanceUntilIdle()

        coVerify(exactly = 0) { useCase(any(), any(), any(), any(), any(), any()) }
        assertFalse(onSavedCalled)
    }

    @Test
    fun saveBook_withValidTitle_callsUseCaseWithCorrectParams() = runTest {
        viewModel.updateTitle("Kotlin入門")
        viewModel.updateAuthor("著者A")
        viewModel.updatePublisher("出版社B")
        viewModel.updatePublishedDate("2024-01-01")
        viewModel.updatePageCount("300")

        viewModel.saveBook {}
        advanceUntilIdle()

        coVerify(exactly = 1) {
            useCase(
                title = "Kotlin入門",
                author = "著者A",
                publisher = "出版社B",
                publishedDate = "2024-01-01",
                pageCount = "300",
                thumbnail = null
            )
        }
    }

    @Test
    fun saveBook_withValidTitle_emitsBookSavedEvent() = runTest {
        viewModel.updateTitle("Kotlin入門")

        viewModel.eventFlow.test {
            viewModel.saveBook {}
            assertEquals(ManualBookUiEvent.BookSaved, awaitItem())
        }
    }

    @Test
    fun saveBook_withValidTitle_callsOnSavedCallback() = runTest {
        viewModel.updateTitle("Kotlin入門")
        var onSavedCalled = false

        viewModel.saveBook { onSavedCalled = true }
        advanceUntilIdle()

        assertTrue(onSavedCalled)
    }
}
