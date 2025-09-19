package com.belltree.readtrack.data.repository

import com.belltree.readtrack.data.local.room.ReadLogDao
import com.belltree.readtrack.domain.model.ReadLog
import com.belltree.readtrack.domain.repository.ReadLogRepository
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
    override suspend fun getLogByBookId(bookId: String): List<ReadLog> =
        readLogDao.getLogByBookId(bookId)

    override fun getReadLogsFlowForMonths(yearMonthIds: List<Int>): Flow<List<ReadLog>> =
        readLogDao.getReadLogsFlowForMonths(yearMonthIds)

    override suspend fun getReadLogsForMonths(yearMonthIds: List<Int>): List<ReadLog> =
        readLogDao.getReadLogsForMonths(yearMonthIds)
}