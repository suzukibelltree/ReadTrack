package com.belltree.readtrack

import com.belltree.readtrack.domain.usecase.SaveManualBookUseCase
import com.belltree.readtrack.ui.registermanually.ManualBookEntryViewModel
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
}
