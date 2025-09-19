package com.belltree.readtrack.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 読書ログを表すデータクラス
 * @param yearMonthId 年月を表すID
 * @param readPages その月に読んだページ数
 */
@Entity(tableName = "ReadLog")
data class ReadLog(
    @PrimaryKey(autoGenerate = true)
    val logId: Int = 0,
    val bookId: String = "",
    val readPages: Int,
    val recordedAt: String,
    val yearMonthId: Int
)

data class ReadLogByMonth(
    val yearMonthId: Int,
    val totalReadPages: Int
)