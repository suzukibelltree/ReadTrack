package com.belltree.readtrack.ui.search.isbn

import androidx.lifecycle.ViewModel
import com.belltree.readtrack.data.repository.BooksRemoteRepository
import com.belltree.readtrack.domain.model.BookItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ISBNSearchViewModel @Inject constructor(
    private val repository: BooksRemoteRepository
) : ViewModel() {
    private val _isbn = MutableStateFlow("")
    val isbn: StateFlow<String> get() = _isbn

    fun setIsbn(isbn: String) {
        _isbn.value = isbn
    }

    suspend fun searchBookByISBN(): BookItem? {
        return repository.searchBooks(
            query = _isbn.value
        ).items?.firstOrNull()
    }
}