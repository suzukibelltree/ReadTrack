package com.belltree.readtrack.app

import android.content.Context
import com.belltree.readtrack.data.local.room.BookDatabase
import com.belltree.readtrack.data.repository.DatabaseBooksRepository
import com.belltree.readtrack.data.repository.DatabaseReadLogRepository
import com.belltree.readtrack.domain.repository.BooksRepository
import com.belltree.readtrack.domain.repository.ReadLogRepository

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
        DatabaseBooksRepository(BookDatabase.Companion.getDatabase(context).bookDao())
    }
    override val readLogRepository: ReadLogRepository by lazy {
        DatabaseReadLogRepository(BookDatabase.Companion.getDatabase(context).readLogDao())
    }
}