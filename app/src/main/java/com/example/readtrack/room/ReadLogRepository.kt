package com.example.readtrack.room

import kotlinx.coroutines.flow.Flow

interface ReadLogRepository {
    suspend fun insertLog(readLog: ReadLog)
    suspend fun updateLog(readLog: ReadLog)
    fun getAllLogs(): Flow<List<ReadLog>>
    suspend fun getLogByMonthId(monthId: Int): ReadLog?
    suspend fun upsertLog(readLog: ReadLog)
}

class DatabaseReadLogRepository(private val readLogDao: ReadLogDao) : ReadLogRepository {
    override suspend fun insertLog(readLog: ReadLog) = readLogDao.insertLog(readLog)
    override suspend fun updateLog(readLog: ReadLog) = readLogDao.updateLog(readLog)
    override fun getAllLogs(): Flow<List<ReadLog>> = readLogDao.getAllLogs()
    override suspend fun getLogByMonthId(monthId: Int): ReadLog? =
        readLogDao.getLogByMonthId(monthId)

    override suspend fun upsertLog(readLog: ReadLog) = readLogDao.upsertLog(readLog)
}