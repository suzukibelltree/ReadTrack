package com.example.readtrack.room

import com.example.readtrack.network.BookData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseBooksRepository@Inject constructor(private val bookDao: BookDao) : BooksRepository {
    override fun getAllBooks(): List<BookData> = bookDao.getAllBooks()
    override suspend fun insert(book: BookData) = bookDao.insert(book)
    override suspend fun updateBook(book: BookData) = bookDao.updateBook(book)
    override suspend fun deleteBook(book: BookData) = bookDao.deleteBook(book)
    override fun getAllBooksFlow(): Flow<List<BookData>> = bookDao.getAllBooksFlow()
    override suspend fun getBookById(bookId: String): BookData? = bookDao.getBookById(bookId)
}