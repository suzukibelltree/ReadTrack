package com.belltree.readtrack.ui.home

import com.belltree.readtrack.domain.model.BookData
import com.belltree.readtrack.domain.model.ReadLogByMonth

object HomeBindingModelConverter {
    fun convertToHomeBindingModel(
        numOfReadBooks: Int,
        newlyAddedBookData: BookData?,
        recentlyReadBookData: BookData?,
        readLogsForGraph: List<ReadLogByMonth>
    ): HomeBindingModel {
        return HomeBindingModel(
            numOfReadBooks = numOfReadBooks,
            newlyAddedBook = convertToHomeBookBindingModel(newlyAddedBookData),
            recentlyReadBook = convertToHomeBookBindingModel(recentlyReadBookData),
            readLogForGraph = readLogsForGraph
        )
    }

    private fun convertToHomeBookBindingModel(bookData: BookData?): HomeBookBindingModel? {
        return bookData?.let {
            HomeBookBindingModel(
                id = it.id,
                title = it.title,
                thumbnail = it.thumbnail,
                registeredDate = it.registeredDate,
                updatedDate = it.updatedDate
            )
        }
    }
}