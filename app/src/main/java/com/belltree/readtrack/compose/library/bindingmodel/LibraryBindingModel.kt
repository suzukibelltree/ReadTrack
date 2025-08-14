package com.belltree.readtrack.compose.library.bindingmodel

data class LibraryBindingModel(
    val libraryBookBindingModel: List<LibraryBookBindingModel>
)

data class LibraryBookBindingModel(
    val id: String,
    val progress: Int,
    val thumbnail: String?,
    val pageCount: Int?,
    val readPages: Int?,
    val registeredDate: String,
    val updatedDate: String
)