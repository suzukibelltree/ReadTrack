package com.belltree.readtrack.compose.myBookDetail.bindingmodel

import com.belltree.readtrack.room.ReadLog

data class MyBookDetailBindingModel(
    val myBookDetailBookBindingModel: MyBookDetailBookBindingModel,
    val readLog: List<ReadLog>
)

data class MyBookDetailBookBindingModel(
    val id: String,
    val title: String,
    val authors: String?,
    val thumbnail: String?,
    val progress: Int,
    val pageCount: Int?,
    val readPages: Int?,
    val comment: String?,
    val registeredDate: String,
    val updatedDate: String
)