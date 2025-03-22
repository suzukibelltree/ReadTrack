package com.belltree.readtrack.room

import android.content.Context

/**
 * アプリケーション全体で使用するリポジトリを提供するコンテナ
 */
interface AppContainer {
    val booksRepository: BooksRepository
    val readLogRepository: ReadLogRepository
}

/**
 * アプリケーション全体で使用するリポジトリを提供するコンテナの実装
 */
class AppDataContainer(private val context: Context) : AppContainer {
    override val booksRepository: BooksRepository by lazy {
        DatabaseBooksRepository(BookDatabase.getDatabase(context).bookDao())
    }
    override val readLogRepository: ReadLogRepository by lazy {
        DatabaseReadLogRepository(BookDatabase.getDatabase(context).readLogDao())
    }
}