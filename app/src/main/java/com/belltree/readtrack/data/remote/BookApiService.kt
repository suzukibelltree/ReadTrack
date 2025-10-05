package com.belltree.readtrack.data.remote

import com.belltree.readtrack.domain.model.BookLists
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Google Books APIを利用して書籍情報を取得するためのインターフェース
 */
interface GoogleBooksApiService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("key") apiKey: String,
        @Query("startIndex") startIndex: Int = 0,
        @Query("maxResults") maxResults: Int = 10
    ): BookLists
}