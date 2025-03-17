package com.belltree.readtrack.room

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 読書ログを表すデータクラス
 * @param yearMonthId 年月を表すID
 * @param readPages その月に読んだページ数
 */
@Entity(tableName = "ReadLog")
data class ReadLog(
    @PrimaryKey
    val yearMonthId: Int = 0,
    val readPages: Int,
)