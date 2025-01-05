package com.example.readtrack

import kotlinx.serialization.Serializable

object Route {
    @Serializable
    data object Home

    @Serializable
    data object Library

    @Serializable
    data object Setting

    @Serializable
    data object RegisterProcess

    @Serializable
    data object Search

    @Serializable
    data class BookDetail(val bookId: String)

    @Serializable
    data class MyBook(val savedBookId: String)
}