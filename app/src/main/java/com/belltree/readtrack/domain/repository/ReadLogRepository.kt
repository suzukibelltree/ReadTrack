package com.belltree.readtrack.domain.repository

import com.belltree.readtrack.domain.model.ReadLog
import kotlinx.coroutines.flow.Flow

interface ReadLogRepository {
    val allLogs: Flow<List<ReadLog>>
    suspend fun insertLog(readLog: ReadLog)
    suspend fun updateLog(readLog: ReadLog)
    fun getAllLogsFlow(): Flow<List<ReadLog>>
    suspend fun getLogByMonthId(monthId: Int): ReadLog?
    suspend fun upsertLog(readLog: ReadLog)
    suspend fun getLogByBookId(bookId: String): List<ReadLog>
    fun getReadLogsFlowForMonths(yearMonthIds: List<Int>): Flow<List<ReadLog>>
    suspend fun getReadLogsForMonths(yearMonthIds: List<Int>): List<ReadLog>
}