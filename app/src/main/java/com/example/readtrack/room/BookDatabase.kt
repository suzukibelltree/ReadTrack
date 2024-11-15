package com.example.readtrack.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.readtrack.network.BookData

@Database(entities = [BookData::class], version = 1)
abstract class BookDatabase :RoomDatabase() {
    abstract  fun bookDao(): BookDao

    companion object{
        @Volatile
        private var INSTANCE: BookDatabase? = null

        fun getDatabase(context: Context): BookDatabase{
            return INSTANCE ?: synchronized(this){
                Room.databaseBuilder(
                    context,BookDatabase::class.java,"book_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}