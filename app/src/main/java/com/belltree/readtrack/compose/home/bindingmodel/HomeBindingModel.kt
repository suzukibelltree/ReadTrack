package com.belltree.readtrack.compose.home.bindingmodel

import com.belltree.readtrack.room.ReadLogByMonth

data class HomeBindingModel(
    val numOfReadBooks: Int,
    val newlyAddedBook: HomeBookBindingModel?,
    val recentlyReadBook: HomeBookBindingModel?,
    val readLogForGraph: List<ReadLogByMonth>
)

data class HomeBookBindingModel(
    val id: String,
    val title: String,
    val thumbnail: String?,
    val registeredDate: String,
    val updatedDate: String
)