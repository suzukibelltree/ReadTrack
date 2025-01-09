package com.example.readtrack.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * ReadLogsViewModelのインスタンスを生成するファクトリ
 */
class ReadLogsViewModelFactory(
    private val readLogRepository: ReadLogRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReadLogsViewModel::class.java)) {
            return ReadLogsViewModel(readLogRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}