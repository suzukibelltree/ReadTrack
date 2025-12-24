package com.belltree.readtrack

import com.belltree.readtrack.domain.repository.BooksRepository
import com.belltree.readtrack.domain.usecase.SaveManualBookUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(JUnit4::class)
class SaveManualBookUseCaseTest {
    val repository = mockk<BooksRepository>(relaxed = true)
    val useCase = SaveManualBookUseCase(repository)

    /**
     * タイトルが入力されたとき、リポジトリのinsertメソッドが呼び出されることを確認するテスト
     */
    @Test
    fun onTitleEntered() {
        runTest {
            val title = "Sample Book Title"
            useCase.invoke(title = title)
            coVerify {
                repository.insert(
                    match { book ->
                        book.title == title
                    },
                )
            }
        }
    }

    /**
     * ページ数に数値以外が入力されたとき、ページ数が0としてリポジトリのinsertメソッドが呼び出されることを確認するテスト
     */
    @Test
    fun onIrregularPageCount() {
        runTest {
            val title = "Sample Book Title"
            val pageCount = "NotNumber"
            useCase.invoke(title = title, pageCount = pageCount)
            coVerify {
                repository.insert(
                    match { book ->
                        book.title == title && book.pageCount == 0
                    },
                )
            }
        }
    }

    /**
     * UUIDが生成されていることを確認するテスト
     */
    @Test
    fun checkUUIDGeneration() {
        runTest {
            val title = "Sample Book Title"
            useCase.invoke(title = title)
            coVerify {
                repository.insert(
                    match { book ->
                        book.id.isNotBlank()
                    },
                )
            }
        }
    }
}