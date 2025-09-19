package com.belltree.readtrack.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.belltree.readtrack.domain.model.ReadLog
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

    @Query("SELECT * FROM ReadLog WHERE bookId = :bookId")
    suspend fun getLogByBookId(bookId: String): List<ReadLog>

    @Query("SELECT * FROM ReadLog WHERE yearMonthId IN (:yearMonthIds)")
    fun getReadLogsFlowForMonths(yearMonthIds: List<Int>): Flow<List<ReadLog>>

    @Query("SELECT * FROM ReadLog WHERE yearMonthId IN (:yearMonthIds)")
    suspend fun getReadLogsForMonths(yearMonthIds: List<Int>): List<ReadLog>
}