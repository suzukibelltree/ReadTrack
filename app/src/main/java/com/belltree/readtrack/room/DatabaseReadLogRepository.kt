package com.belltree.readtrack.room

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseReadLogRepository @Inject constructor(private val readLogDao: ReadLogDao) :
    ReadLogRepository {
    override val allLogs: Flow<List<ReadLog>> = readLogDao.getAllLogs()
    override suspend fun insertLog(readLog: ReadLog) = readLogDao.insertLog(readLog)
    override suspend fun updateLog(readLog: ReadLog) = readLogDao.updateLog(readLog)
    override suspend fun getLogByMonthId(monthId: Int): ReadLog? =
        readLogDao.getLogByMonthId(monthId)

    override fun getAllLogsFlow(): Flow<List<ReadLog>> = readLogDao.getAllLogs()
    override suspend fun upsertLog(readLog: ReadLog) = readLogDao.upsertLog(readLog)
}