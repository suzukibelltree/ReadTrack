package com.belltree.readtrack.domain.usecase

import com.belltree.readtrack.domain.model.BookData
import com.belltree.readtrack.domain.repository.BooksRepository
import javax.inject.Inject

class GetAllBooksUseCase @Inject constructor(
    private val booksRepository: BooksRepository
) {
    suspend operator fun invoke(): List<BookData> {
        return booksRepository.getAllBooks()
    }
}
