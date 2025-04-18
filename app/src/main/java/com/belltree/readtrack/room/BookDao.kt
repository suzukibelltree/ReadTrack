package com.belltree.readtrack.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.belltree.readtrack.network.BookData
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: BookData)

    @Update
    suspend fun updateBook(book: BookData)

    @Delete
    suspend fun deleteBook(book: BookData)

    @Query("SELECT * FROM BookData")
    fun getAllBooks(): List<BookData>

    @Query("SELECT * FROM BookData")
    fun getAllBooksFlow(): Flow<List<BookData>>

    @Query("SELECT * FROM BookData WHERE id = :bookId")
    suspend fun getBookById(bookId: String): BookData?

    @Query("SELECT id FROM BookData")
    suspend fun getAllBookIds(): List<String>
}