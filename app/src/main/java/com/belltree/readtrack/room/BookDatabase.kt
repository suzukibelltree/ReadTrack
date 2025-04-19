package com.belltree.readtrack.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.belltree.readtrack.network.BookData

@Database(entities = [BookData::class, ReadLog::class], version = 1)
abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun readLogDao(): ReadLogDao

    companion object {
        @Volatile
        private var INSTANCE: BookDatabase? = null

        fun getDatabase(context: Context): BookDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context, BookDatabase::class.java, "books_database"
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}