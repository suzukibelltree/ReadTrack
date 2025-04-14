package com.belltree.readtrack.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.belltree.readtrack.network.BookData

@Database(entities = [BookData::class, ReadLog::class], version = 2)
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
                    .addMigrations(Migration_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private val Migration_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE ReadLog_new (
                        logId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        bookId TEXT NOT NULL,
                        readPages INTEGER NOT NULL,
                        recordedAt TEXT NOT NULL,
                        yearMonthId INTEGER NOT NULL
                    )
                """.trimIndent()
                )

                database.execSQL(
                    """
                    INSERT INTO ReadLog_new (bookId, readPages, recordedAt, yearMonthId)
                    SELECT 'unknown', readPages, '1970-01-01', yearMonthId FROM ReadLog
                """.trimIndent()
                )

                database.execSQL("DROP TABLE ReadLog")
                database.execSQL("ALTER TABLE ReadLog_new RENAME TO ReadLog")
            }
        }
    }
}