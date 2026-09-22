package com.belltree.readtrack

import com.belltree.readtrack.domain.model.BookData
import com.belltree.readtrack.domain.repository.BooksRepository
import com.belltree.readtrack.domain.usecase.GetRecentlyUpdatedBookUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GetRecentlyUpdatedBookUseCaseTest {
    val repository = mockk<BooksRepository>(relaxed = true)
    val useCase = GetRecentlyUpdatedBookUseCase(repository)

    private fun book(id: String, updatedDate: String) = BookData(
        id = id,
        title = "Test Book $id",
        author = "Test Author",
        publisher = "Test Publisher",
        publishedDate = "2023-01-01",
        pageCount = 100,
        progress = 0,
        readpage = 0,
        comment = "",
        updatedDate = updatedDate,
        description = "",
        thumbnail = ""
    )

    /**
     * 書籍が1件もない場合はnullを返すことを確認するテスト
     */
    @Test
    fun returnsNullWhenThereAreNoBooks() {
        runTest {
            coEvery { repository.getAllBooks() } returns emptyList()

            assertNull(useCase())
        }
    }

    /**
     * 最もupdatedDateが新しい書籍を返すことを確認するテスト
     * (リストの先頭・末尾以外に最新の書籍がある場合も含む)
     */
    @Test
    fun returnsTheBookWithTheMostRecentUpdatedDate() {
        runTest {
            val books = listOf(
                book(id = "book-1", updatedDate = "2023-01-01"),
                book(id = "book-2", updatedDate = "2023-03-01"),
                book(id = "book-3", updatedDate = "2023-02-01")
            )
            coEvery { repository.getAllBooks() } returns books

            assertEquals("book-2", useCase()?.id)
        }
    }
}
