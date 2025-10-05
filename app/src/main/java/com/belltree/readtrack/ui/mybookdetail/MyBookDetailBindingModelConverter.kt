package com.belltree.readtrack.ui.mybookdetail

import com.belltree.readtrack.domain.model.ReadLog

object MyBookDetailBindingModelConverter {
    fun convertToMyBookDetailBindingModel(
        myBookDetailBookBindingModel: MyBookDetailBookBindingModel,
        readLog: List<ReadLog>
    ): MyBookDetailBindingModel {
        return MyBookDetailBindingModel(
            myBookDetailBookBindingModel = myBookDetailBookBindingModel,
            readLog = readLog
        )
    }

    fun convertToMyBookDetailBookBindingModel(
        id: String,
        title: String,
        authors: String?,
        thumbnail: String?,
        progress: Int,
        pageCount: Int?,
        readPages: Int?,
        comment: String?,
        registeredDate: String,
        updatedDate: String
    ): MyBookDetailBookBindingModel {
        return MyBookDetailBookBindingModel(
            id = id,
            title = title,
            authors = authors,
            thumbnail = thumbnail,
            progress = progress,
            pageCount = pageCount,
            readPages = readPages,
            comment = comment,
            registeredDate = registeredDate,
            updatedDate = updatedDate
        )
    }
}