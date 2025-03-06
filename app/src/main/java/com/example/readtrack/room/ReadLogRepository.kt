package com.example.readtrack.room

import kotlinx.coroutines.flow.Flow

interface ReadLogRepository {
    val allLogs: Flow<List<ReadLog>>
    suspend fun insertLog(readLog: ReadLog)
    suspend fun updateLog(readLog: ReadLog)
    fun getAllLogsFlow(): Flow<List<ReadLog>>
    suspend fun getLogByMonthId(monthId: Int): ReadLog?
    suspend fun upsertLog(readLog: ReadLog)
}
