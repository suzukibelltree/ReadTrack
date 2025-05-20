package com.belltree.readtrack

import kotlinx.serialization.Serializable

object Route {
    @Serializable
    data object Login

    @Serializable
    data object Home

    @Serializable
    data object Library

    @Serializable
    data object Setting

    @Serializable
    data object RegisterProcess

    @Serializable
    data object BarcodeScanner

    @Serializable
    data object RegisterManually

    @Serializable
    data object Search

    @Serializable
    data class BookDetail(val bookId: String)

    @Serializable
    data class MyBook(val savedBookId: String)
}