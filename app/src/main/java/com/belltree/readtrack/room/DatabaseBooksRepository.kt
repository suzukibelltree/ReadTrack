package com.belltree.readtrack.room

import com.belltree.readtrack.network.BookData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseBooksRepository @Inject constructor(private val bookDao: BookDao) : BooksRepository {
    override val allBooks: Flow<List<BookData>> = bookDao.getAllBooksFlow()
    override suspend fun getAllBooks(): List<BookData> = bookDao.getAllBooks()
    override suspend fun insert(book: BookData) = bookDao.insert(book)
    override suspend fun updateBook(book: BookData) = bookDao.updateBook(book)
    override suspend fun deleteBook(book: BookData) = bookDao.deleteBook(book)
    override fun getAllBooksFlow(): Flow<List<BookData>> = bookDao.getAllBooksFlow()
    override suspend fun getBookById(bookId: String): BookData? = bookDao.getBookById(bookId)
    override fun getBookByIdFlow(bookId: String): Flow<BookData?> = bookDao.getBookByIdFlow(bookId)
    override suspend fun getAllBookIds(): List<String> = bookDao.getAllBookIds()
}