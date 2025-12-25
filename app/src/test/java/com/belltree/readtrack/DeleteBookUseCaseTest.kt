package com.belltree.readtrack

import com.belltree.readtrack.domain.model.BookData
import com.belltree.readtrack.domain.repository.BooksRepository
import com.belltree.readtrack.domain.usecase.DeleteBookUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DeleteBookUseCaseTest {
    val repository = mockk<BooksRepository>(relaxed = true)
    val useCase = DeleteBookUseCase(repository)

    /**
     * 存在するBookIdで書籍情報が削除されるかのテスト
     */
    @Test
    fun deleteBookByExistingBookId() {
        runTest {
            val bookId = "book-1"
            val book = BookData(
                id = bookId,
                title = "Test Book",
                author = "Test Author",
                publisher = "Test Publisher",
                publishedDate = "2023-01-01",
                pageCount = 100,
                progress = 0,
                readpage = 0,
                comment = "",
                updatedDate = "2023-01-01",
                description = "",
                thumbnail = ""
            )

            coEvery { repository.getBookById(bookId) } returns book

            useCase(bookId)

            coVerify(exactly = 1) {
                repository.deleteBook(book)
            }
        }
    }

    /**
     * 存在しないBookIdで削除処理が行われないことを確認するテスト
     */
    @Test
    fun deleteBookByNonExistingBookId() {
        runTest {
            val bookId = "missing"
            coEvery { repository.getBookById(bookId) } returns null

            useCase(bookId)

            coVerify(exactly = 0) {
                repository.deleteBook(any())
            }
        }
    }
}