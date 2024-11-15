package com.example.readtrack.room

import com.example.readtrack.network.BookData

class DatabaseBooksRepository(private val bookDao: BookDao) : BooksRepository {
    override fun getAllBooks(): List<BookData> = bookDao.getAllBooks()
    override suspend fun insert(book: BookData) = bookDao.insert(book)
    override suspend fun update(book: BookData) = bookDao.update(book)
    override suspend fun delete(book: BookData) = bookDao.delete(book)
}