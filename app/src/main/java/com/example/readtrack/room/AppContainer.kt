package com.example.readtrack.room

import android.content.Context

/**
 * アプリケーション全体で使用するリポジトリを提供するコンテナ
 */
interface AppContainer {
    val booksRepository: BooksRepository
}

/**
 * アプリケーション全体で使用するリポジトリを提供するコンテナの実装
 */
class AppDataContainer(private val context: Context) : AppContainer {
    override val booksRepository: BooksRepository by lazy {
        DatabaseBooksRepository(BookDatabase.getDatabase(context).bookDao())
    }
}