package com.belltree.readtrack.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Login : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object Library : Route

    @Serializable
    data object Setting : Route

    @Serializable
    data object RegisterProcess : Route

    @Serializable
    data object BarcodeScanner : Route

    @Serializable
    data object RegisterManually : Route

    @Serializable
    data object Search : Route

    @Serializable
    data class BookDetail(val bookId: String) : Route

    @Serializable
    data class MyBook(val savedBookId: String) : Route
}