package com.example.readtrack.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ReadLogの状態を管理するViewModel
 */
@HiltViewModel
class ReadLogsViewModel @Inject constructor(
    private val readLogRepository: ReadLogRepository
) : ViewModel() {
    val allLogs: StateFlow<List<ReadLog>> = readLogRepository.getAllLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _allLogs = MutableStateFlow<ReadLog?>(null)
    suspend fun insertLog(readLog: ReadLog) {
        readLogRepository.insertLog(readLog)
    }

    suspend fun updateLog(readLog: ReadLog) {
        readLogRepository.updateLog(readLog)
    }

    fun getAllLogs(): Flow<List<ReadLog>> {
        return readLogRepository.getAllLogs()
    }

    suspend fun getLogByMonthId(monthId: Int): ReadLog? {
        return readLogRepository.getLogByMonthId(monthId)
    }

    suspend fun upsertLog(readLog: ReadLog) {
        readLogRepository.upsertLog(readLog)
    }

    fun upsertLogInViewModelScope(log: ReadLog) {
        viewModelScope.launch {
            upsertLog(log)
        }
    }
}