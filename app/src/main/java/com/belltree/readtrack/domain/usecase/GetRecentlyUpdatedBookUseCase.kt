package com.belltree.readtrack.domain.usecase

import com.belltree.readtrack.domain.model.BookData
import com.belltree.readtrack.domain.repository.BooksRepository
import javax.inject.Inject

class GetRecentlyUpdatedBookUseCase @Inject constructor(
    private val booksRepository: BooksRepository
) {
    suspend operator fun invoke(): BookData? {
        return booksRepository.getAllBooks().maxByOrNull { it.updatedDate }
    }
}
