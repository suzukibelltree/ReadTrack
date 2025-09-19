package com.belltree.readtrack.ui.library

import com.belltree.readtrack.domain.model.BookData

object LibraryBindingModelConverter {
    fun convertToLibraryBindingModel(
        libraryBookBindingModels: List<LibraryBookBindingModel>
    ): LibraryBindingModel {
        return LibraryBindingModel(
            libraryBookBindingModel = libraryBookBindingModels
        )
    }

    fun convertToLibraryBookBindingModel(
        bookData: BookData
    ): LibraryBookBindingModel {
        return LibraryBookBindingModel(
            id = bookData.id,
            progress = bookData.progress,
            thumbnail = bookData.thumbnail,
            pageCount = bookData.pageCount,
            readPages = bookData.readpage,
            registeredDate = bookData.registeredDate,
            updatedDate = bookData.updatedDate
        )
    }
}