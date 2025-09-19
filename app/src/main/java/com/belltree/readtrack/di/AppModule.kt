package com.belltree.readtrack.di

import android.content.Context
import androidx.room.Room
import com.belltree.readtrack.data.local.room.BookDao
import com.belltree.readtrack.data.local.room.BookDatabase
import com.belltree.readtrack.data.local.room.ReadLogDao
import com.belltree.readtrack.data.repository.DatabaseBooksRepository
import com.belltree.readtrack.data.repository.DatabaseReadLogRepository
import com.belltree.readtrack.domain.repository.BooksRepository
import com.belltree.readtrack.domain.repository.ReadLogRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BookDatabase {
        return Room.databaseBuilder(
            context,
            BookDatabase::class.java,
            "books_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBooksRepository(booksDao: BookDao): BooksRepository {
        return DatabaseBooksRepository(booksDao) // 実際の実装クラスを返す
    }

    @Provides
    fun provideBooksDao(database: BookDatabase): BookDao {
        return database.bookDao()
    }

    @Provides
    @Singleton
    fun provideReadLogRepository(readLogDao: ReadLogDao): ReadLogRepository {
        return DatabaseReadLogRepository(readLogDao) // 実際の実装クラスを返す
    }

    @Provides
    fun provideReadLogDao(database: BookDatabase): ReadLogDao {
        return database.readLogDao()
    }
}
