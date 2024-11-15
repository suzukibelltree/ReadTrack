package com.example.readtrack.room

import android.content.Context

interface AppContainer {
    val booksRepository: BooksRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val booksRepository: BooksRepository by lazy {
        DatabaseBooksRepository(BookDatabase.getDatabase(context).bookDao())
    }
}