package com.belltree.readtrack.domain.usecase

import com.belltree.readtrack.domain.repository.BooksRepository
import javax.inject.Inject

class UpdateBookUseCase @Inject constructor(
    private val booksRepository: BooksRepository
) {
    suspend operator fun invoke(
        bookId: String,
        progress: Int,
        readPages: Int,
        comment: String?,
        updatedDate: String
    ) {
        val currentBook = booksRepository.getBookById(bookId) ?: return
        val updatedBook = currentBook.copy(
            progress = progress,
            readpage = readPages,
            comment = comment,
            updatedDate = updatedDate
        )
        booksRepository.updateBook(updatedBook)
    }
}
