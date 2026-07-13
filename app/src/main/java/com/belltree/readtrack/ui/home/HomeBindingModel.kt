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
) {
    /**
     * 本が1冊も登録されていない状態(ウェルカム表示の判定に使用)
     */
    val isLibraryEmpty: Boolean
        get() = numOfReadBooks == 0 &&
                newlyAddedBook == null &&
                recentlyReadBook == null
}

data class HomeBookBindingModel(
    val id: String,
    val title: String,
    val thumbnail: String?,
    val registeredDate: String,
    val updatedDate: String
)