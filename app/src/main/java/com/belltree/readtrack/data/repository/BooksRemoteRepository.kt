package com.belltree.readtrack.data.repository

import com.belltree.readtrack.BuildConfig
import com.belltree.readtrack.data.remote.BookPagingSource
import com.belltree.readtrack.data.remote.GoogleBooksApiService
import com.belltree.readtrack.domain.model.BookLists
import javax.inject.Inject

class BooksRemoteRepository @Inject constructor(
    private val apiService: GoogleBooksApiService
) {
    fun getBookPagingSource(query: String): BookPagingSource {
        return BookPagingSource(
            bookApiService = apiService,
            query = query,
            apiKey = BuildConfig.API_KEY
        )
    }

    suspend fun searchBooks(
        query: String,
        apiKey: String = BuildConfig.API_KEY,
        startIndex: Int = 0,
        maxResults: Int = 10
    ): BookLists {
        val result = apiService.searchBooks(query, apiKey, startIndex, maxResults)
        return result
    }

    suspend fun getBookById(
        volumeId: String,
        apiKey: String = BuildConfig.API_KEY
    ) = apiService.getBookById(volumeId, apiKey)
}