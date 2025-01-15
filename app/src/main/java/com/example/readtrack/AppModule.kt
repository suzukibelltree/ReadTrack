package com.example.readtrack

import android.content.Context
import androidx.room.Room
import com.example.readtrack.room.BookDao
import com.example.readtrack.room.BookDatabase
import com.example.readtrack.room.BooksRepository
import com.example.readtrack.room.DatabaseBooksRepository
import com.example.readtrack.room.DatabaseReadLogRepository
import com.example.readtrack.room.ReadLogDao
import com.example.readtrack.room.ReadLogRepository
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
    fun provideBooksDao(database:BookDatabase): BookDao {
        return database.bookDao()
    }

    @Provides
    fun provideReadLogDao(database: BookDatabase): ReadLogDao {
        return database.readLogDao()
    }

    @Provides
    @Singleton
    fun provideReadLogRepository(readLogDao: ReadLogDao): ReadLogRepository {
        return DatabaseReadLogRepository(readLogDao) // 実際の実装クラスを返す
    }
}
