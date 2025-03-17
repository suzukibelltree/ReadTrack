package com.belltree.readtrack.room

import com.belltree.readtrack.network.BookData
import kotlinx.coroutines.flow.Flow

interface BooksRepository {
    val allBooks: Flow<List<BookData>>
    fun getAllBooks(): List<BookData>
    suspend fun insert(book: BookData)
    suspend fun updateBook(book: BookData)
    suspend fun deleteBook(book: BookData)
    fun getAllBooksFlow(): Flow<List<BookData>>
    suspend fun getBookById(bookId: String): BookData?
}