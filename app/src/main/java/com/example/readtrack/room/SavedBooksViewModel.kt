package com.example.readtrack.room

import androidx.activity.result.launch
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readtrack.network.BookData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavedBooksViewModel(private val booksRepository: BooksRepository) : ViewModel() {
    val savedBooks: StateFlow<List<BookData>> = booksRepository.getAllBooksFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
}