package com.example.readtrack.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.readtrack.network.BookData

@Dao
interface BookDao {
    @Insert
    suspend fun insert(book: BookData)

    @Update
    suspend fun update(book: BookData)

    @Delete
    suspend fun delete(book: BookData)

    @Query("SELECT * FROM BookData")
    fun getAllBooks(): List<BookData>
}