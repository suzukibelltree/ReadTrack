package com.belltree.readtrack.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadLogDao {
    @Insert
    suspend fun insertLog(readLog: ReadLog)

    @Update
    suspend fun updateLog(readLog: ReadLog)

    @Upsert
    suspend fun upsertLog(readLog: ReadLog)

    @Query("SELECT * FROM ReadLog")
    fun getAllLogs(): Flow<List<ReadLog>>

    @Query("SELECT * FROM ReadLog WHERE yearMonthId = :monthId")
    suspend fun getLogByMonthId(monthId: Int): ReadLog?
}