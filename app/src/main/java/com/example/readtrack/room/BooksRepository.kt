package com.example.readtrack.room

import com.example.readtrack.network.BookData
import kotlinx.coroutines.flow.Flow

interface BooksRepository{
    fun getAllBooks(): List<BookData>
    suspend fun insert(book: BookData)
    suspend fun updateBook(book: BookData)
    suspend fun deleteBook(book: BookData)
    fun getAllBooksFlow(): Flow<List<BookData>>
    suspend fun getBookById(bookId: String): BookData?
}