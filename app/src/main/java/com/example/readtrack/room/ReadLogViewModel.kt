package com.example.readtrack.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readtrack.network.BookData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ReadLogの状態を管理するViewModel
 */
@HiltViewModel
class ReadLogsViewModel @Inject constructor(
    private val savedBooksRepository: BooksRepository,
    private val readLogRepository: ReadLogRepository
) : ViewModel() {
    // ローカルに保存されている本の情報
    private val _allBooks = MutableStateFlow<BookData?>(null)
    val allBooks: StateFlow<List<BookData>> = savedBooksRepository.allBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // ローカルに保存されている読書ログの情報
    private val _allLogs = MutableStateFlow<ReadLog?>(null)
    val allLogs: StateFlow<List<ReadLog>> = readLogRepository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun getAllLogs(): Flow<List<ReadLog>> {
        return readLogRepository.allLogs
    }
}