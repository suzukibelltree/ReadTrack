package com.belltree.readtrack.domain.usecase

import com.belltree.readtrack.domain.repository.BooksRepository
import javax.inject.Inject

class DeleteBookUseCase @Inject constructor(
    private val booksRepository: BooksRepository,
) {
    suspend operator fun invoke(bookId: String) {
        val book = booksRepository.getBookById(bookId) ?: return
        booksRepository.deleteBook(book)
    }
}
