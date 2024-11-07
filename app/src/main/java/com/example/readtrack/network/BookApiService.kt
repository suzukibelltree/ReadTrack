package com.example.readtrack.network

import retrofit2.http.GET
import retrofit2.http.Query

//書籍タイトルによる検索に利用する
interface GoogleBooksApiService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("key") apiKey: String,
    ): BookLists
}
