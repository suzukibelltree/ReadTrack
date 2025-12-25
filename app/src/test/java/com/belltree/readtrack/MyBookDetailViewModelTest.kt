package com.belltree.readtrack

import app.cash.turbine.test
import com.belltree.readtrack.domain.model.BookData
import com.belltree.readtrack.domain.repository.BooksRepository
import com.belltree.readtrack.domain.repository.ReadLogRepository
import com.belltree.readtrack.domain.usecase.DeleteBookUseCase
import com.belltree.readtrack.domain.usecase.InsertReadLogUseCase
import com.belltree.readtrack.domain.usecase.UpdateBookUseCase
import com.belltree.readtrack.ui.mybookdetail.MyBookDetailUiState
import com.belltree.readtrack.ui.mybookdetail.MyBookDetailViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyBookDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val booksRepository: BooksRepository = mockk()
    private val readLogRepository: ReadLogRepository = mockk()
    private val updateBookUseCase: UpdateBookUseCase = mockk(relaxed = true)
    private val insertReadLogUseCase: InsertReadLogUseCase = mockk(relaxed = true)
    private val deleteBookUseCase: DeleteBookUseCase = mockk(relaxed = true)

    private lateinit var viewModel: MyBookDetailViewModel

    @Before
    fun setup() {
        viewModel = MyBookDetailViewModel(
            booksRepository = booksRepository,
            readLogRepository = readLogRepository,
            updateBookUseCase = updateBookUseCase,
            insertReadLogUseCase = insertReadLogUseCase,
            deleteBookUseCase = deleteBookUseCase
        )
    }

    /**
     * 書籍IDを設定すると、書籍情報が読み込まれ、Success状態がemitされることを確認するテスト
     */
    @Test
    fun setBookId_loadsBookAndEmitsSuccess() = runTest {
        // Given
        val bookId = "book-1"
        val book = BookData(
            id = bookId,
            title = "Sample Book",
            author = "Author",
            publisher = "Publisher",
            publishedDate = "2023-01-01",
            description = "Description",
            thumbnail = "",
            progress = 30,
            pageCount = 300,
            readpage = 90,
            comment = "",
            registeredDate = "",
            updatedDate = ""
        )

        coEvery { booksRepository.getBookByIdFlow(bookId) } returns flowOf(book)
        coEvery { readLogRepository.getAllLogsFlow() } returns flowOf(emptyList())

        // When / Then
        viewModel.uiState.test {
            // 初期状態
            assert(awaitItem() is MyBookDetailUiState.Loading)

            // 書籍ID設定
            viewModel.setBookId(bookId)

            val state = awaitItem()
            assert(state is MyBookDetailUiState.Success)
        }
    }

    // --------------------------------------------------
    // 正常系: 読書状態(progress)の変更が保存される
    // --------------------------------------------------
    @Test
    fun updateBook_savesProgressChange() = runTest {
        // Given
        val bookId = "book-1"
        viewModel.setBookId(bookId)

        // When
        viewModel.updateBook(
            progress = 80,
            readPages = 120,
            comment = null,
            updatedDate = "2025/12/24"
        )

        advanceUntilIdle()

        // Then
        coVerify {
            updateBookUseCase(
                bookId = bookId,
                progress = 80,
                readPages = 120,
                comment = null,
                updatedDate = "2025/12/24"
            )
        }
    }

    /**
     * ページ数の変更が保存されることを確認するテスト
     */
    @Test
    fun updateBook_savesReadPagesChange() = runTest {
        // Given
        val bookId = "book-1"
        viewModel.setBookId(bookId)

        // When
        viewModel.updateBook(
            progress = 100,
            readPages = 300,
            comment = "finished",
            updatedDate = "2025/12/24"
        )

        advanceUntilIdle()

        // Then
        coVerify {
            updateBookUseCase(
                bookId = bookId,
                progress = 100,
                readPages = 300,
                comment = "finished",
                updatedDate = "2025/12/24"
            )
        }
    }

}
