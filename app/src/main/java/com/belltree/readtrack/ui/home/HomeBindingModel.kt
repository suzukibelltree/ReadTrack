package com.belltree.readtrack.ui.home

import com.belltree.readtrack.domain.model.ReadLogByMonth

/**
 * ホーム画面で表示するデータのバインディングモデル
 */
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