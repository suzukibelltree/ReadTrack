package com.example.readtrack.network

data class BookLists(
    val items: List<BookItem>?
)

data class BookItem(
    val id: String,
    val selfLink: String,
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    val imageLinks: ImageLinks = ImageLinks("", ""),
    val pageCount: Int?,
    val language: String?,
    val averageRating: Double?,
    val ratingsCount: Int?,
)

data class ImageLinks(
    val smallThumbnail: String,
    val thumbnail: String
)