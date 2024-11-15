package com.example.readtrack.room

import com.example.readtrack.network.BookData

interface BooksRepository{
    fun getAllBooks(): List<BookData>
    suspend fun insert(book: BookData)
    suspend fun update(book: BookData)
    suspend fun delete(book: BookData)
}