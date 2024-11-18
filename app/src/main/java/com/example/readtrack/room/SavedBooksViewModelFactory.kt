package com.example.readtrack.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SavedBooksViewModelFactory(
    private val booksRepository: BooksRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavedBooksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SavedBooksViewModel(booksRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}