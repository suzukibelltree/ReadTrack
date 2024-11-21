package com.example.readtrack.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.readtrack.network.BookData
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: BookData)

    @Update
    suspend fun update(book: BookData)

    @Delete
    suspend fun delete(book: BookData)

    @Query("SELECT * FROM BookData")
    fun getAllBooks(): List<BookData>

    @Query("SELECT * FROM BookData")
    fun getAllBooksFlow(): Flow<List<BookData>>

    // In BookDao.kt
    @Query("SELECT * FROM BookData WHERE id = :bookId")
    suspend fun getBookById(bookId: String): BookData?
}