package com.belltree.readtrack.domain.usecase

import com.belltree.readtrack.core.getCurrentFormattedTime
import com.belltree.readtrack.domain.model.BookData
import com.belltree.readtrack.domain.repository.BooksRepository
import java.util.UUID
import javax.inject.Inject

class SaveManualBookUseCase @Inject constructor(
    private val booksRepository: BooksRepository
) {
    suspend operator fun invoke(
        title: String,
        author: String,
        publisher: String?,
        publishedDate: String?,
        pageCount: String,
        thumbnail: String?
    ) {
        val book = BookData(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            author = author.trim(),
            publisher = publisher.takeIf { !it.isNullOrBlank() },
            publishedDate = publishedDate.takeIf { !it.isNullOrBlank() },
            description = null,
            thumbnail = thumbnail,
            pageCount = pageCount.toIntOrNull() ?: 0,
            registeredDate = getCurrentFormattedTime(),
            updatedDate = getCurrentFormattedTime()
        )
        booksRepository.insert(book)
    }
}

