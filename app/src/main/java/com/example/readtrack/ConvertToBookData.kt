package com.example.readtrack

import com.example.readtrack.network.BookData
import com.example.readtrack.network.BookItem

fun ConvertBookItemToBookData(bookItem: BookItem): BookData {
    return BookData(
        id = bookItem.id,
        title = bookItem.volumeInfo.title,
        author = bookItem.volumeInfo.authors?.get(0) ?: "",
        publisher = bookItem.volumeInfo.publisher,
        publishedDate = bookItem.volumeInfo.publishedDate,
        description = bookItem.volumeInfo.description,
        thumbnail = bookItem.volumeInfo.imageLinks.thumbnail,
        pageCount = bookItem.volumeInfo.pageCount,
        registeredDate = getCurrentFormattedTime(),
        updatedDate = getCurrentFormattedTime()
    )
}