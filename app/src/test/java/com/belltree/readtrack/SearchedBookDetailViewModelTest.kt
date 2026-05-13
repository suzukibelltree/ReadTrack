package com.belltree.readtrack

import com.belltree.readtrack.data.repository.BooksRemoteRepository
import com.belltree.readtrack.domain.model.BookItem
import com.belltree.readtrack.domain.model.VolumeInfo
import com.belltree.readtrack.domain.repository.BooksRepository
import com.belltree.readtrack.ui.search.SearchedBookDetailViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchedBookDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val booksRepository: BooksRepository = mockk(relaxed = true)
    private val booksRemoteRepository: BooksRemoteRepository = mockk(relaxed = true)
    private lateinit var viewModel: SearchedBookDetailViewModel

    @Before
    fun setup() {
        viewModel = SearchedBookDetailViewModel(booksRepository, booksRemoteRepository)
    }

    private fun buildBookItem(
        id: String = "book-1",
        title: String = "Test Book",
        authors: List<String>? = listOf("Author A")
    ) = BookItem(
        id = id,
        selfLink = "https://books.google.com/$id",
        volumeInfo = VolumeInfo(
            title = title,
            authors = authors,
            publisher = null,
            publishedDate = null,
            description = null,
            imageLinks = null,
            pageCount = null,
            categories = null
        )
    )

    // --- loadBookById ---

    @Test
    fun `loadBookById sets isRegistered to true when bookId is already registered`() = runTest {
        coEvery { booksRepository.getAllBookIds() } returns listOf("book-1", "book-2")
        coEvery { booksRemoteRepository.getBookById(any()) } returns buildBookItem()

        viewModel.loadBookById("book-1")
        advanceUntilIdle()

        assertTrue(viewModel.isRegistered.value)
    }

    @Test
    fun `loadBookById sets isRegistered to false when bookId is not registered`() = runTest {
        coEvery { booksRepository.getAllBookIds() } returns listOf("book-2", "book-3")
        coEvery { booksRemoteRepository.getBookById(any()) } returns buildBookItem()

        viewModel.loadBookById("book-1")
        advanceUntilIdle()

        assertFalse(viewModel.isRegistered.value)
    }

    @Test
    fun `loadBookById sets isRegistered to false when no books are registered`() = runTest {
        coEvery { booksRepository.getAllBookIds() } returns emptyList()
        coEvery { booksRemoteRepository.getBookById(any()) } returns buildBookItem()

        viewModel.loadBookById("book-1")
        advanceUntilIdle()

        assertFalse(viewModel.isRegistered.value)
    }

    @Test
    fun `loadBookById updates bookItem with the remote result`() = runTest {
        val expected = buildBookItem(id = "book-1", title = "Kotlin in Action")
        coEvery { booksRepository.getAllBookIds() } returns emptyList()
        coEvery { booksRemoteRepository.getBookById("book-1") } returns expected

        viewModel.loadBookById("book-1")
        advanceUntilIdle()

        assertEquals(expected, viewModel.bookItem.value)
    }

    // --- addBook ---

    @Test
    fun `addBook calls repository insert with BookData converted from BookItem`() = runTest {
        val bookItem = buildBookItem(id = "book-1", title = "Clean Architecture", authors = listOf("Robert C. Martin"))

        viewModel.addBook(bookItem)
        advanceUntilIdle()

        coVerify(exactly = 1) {
            booksRepository.insert(
                match { book ->
                    book.id == "book-1" &&
                    book.title == "Clean Architecture" &&
                    book.author == "Robert C. Martin"
                }
            )
        }
    }

    @Test
    fun `addBook calls repository insert once per call`() = runTest {
        val bookItem = buildBookItem()

        viewModel.addBook(bookItem)
        advanceUntilIdle()

        coVerify(exactly = 1) { booksRepository.insert(any()) }
    }
}