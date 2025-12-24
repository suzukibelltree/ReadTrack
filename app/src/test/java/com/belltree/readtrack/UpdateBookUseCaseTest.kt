package com.belltree.readtrack

import com.belltree.readtrack.domain.model.BookData
import com.belltree.readtrack.domain.repository.BooksRepository
import com.belltree.readtrack.domain.usecase.UpdateBookUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UpdateBookUseCaseTest {
    val repository = mockk<BooksRepository>(relaxed = true)
    val useCase = UpdateBookUseCase(repository)

    /**
     * 存在するBookIdで書籍情報が更新されるかのテスト
     */
    @Test
    fun updateByExistingBookId() {
        runTest {
            val originalBook = BookData(
                id = "123",
                title = "Sample Book",
                author = "Author Name",
                publisher = "Publisher Name",
                publishedDate = "2023-01-01",
                pageCount = 300,
                progress = 0,
                readpage = 0,
                comment = "",
                updatedDate = "2025-12-01",
                description = "",
                thumbnail = ""
            )

            coEvery { repository.getBookById("123") } returns originalBook

            useCase(
                bookId = "123",
                progress = 1,
                readPages = 50,
                comment = "",
                updatedDate = "2025-12-10"
            )

            val slot = slot<BookData>()
            coVerify(exactly = 1) {
                repository.updateBook(capture(slot))
            }

            val updated = slot.captured
            assertEquals("123", updated.id)
            assertEquals(1, updated.progress)
            assertEquals(50, updated.readpage)
            assertEquals("2025-12-10", updated.updatedDate)

        }
    }

    /**
     * 存在しないBookIdで更新しようとしたとき、updateBookが呼び出されないことを確認するテスト
     */
    @Test
    fun updateByUnusualBookId() {
        runTest {
            // Given
            coEvery { repository.getBookById("missing") } returns null

            // When
            useCase(
                bookId = "missing",
                progress = 50,
                readPages = 200,
                comment = "comment",
                updatedDate = "2025/12/24"
            )

            // Then
            coVerify(exactly = 0) {
                repository.updateBook(any())
            }

        }
    }
}