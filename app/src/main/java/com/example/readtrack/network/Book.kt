package com.example.readtrack.network

import androidx.room.Entity
import androidx.room.PrimaryKey

data class BookLists(
    val items: List<BookItem>?
)

data class BookItem(
    val id: String,
    val selfLink: String,
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    val imageLinks: ImageLinks = ImageLinks("", ""),
    val pageCount: Int?,
    val categories: List<String>?,
)

data class ImageLinks(
    val smallThumbnail: String,
    val thumbnail: String
)

// データベースに保存するためのデータクラス
// BookItemはネストされたデータクラスであるため、Roomでのエラーを回避するために分離する
@Entity(tableName = "BookData")
data class BookData(
    @PrimaryKey val id: String,
    val title: String,
    val author:String,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    val thumbnail: String,
    val pageCount: Int?,
    var readpage: Int? = 0, //読んだページ数
    var comment: String? = "", //本に対するコメント、感想
    var progress: Int = 0, //登録された本の状態(0:未読,1:読書中,2:読了)
    var registeredDate: String = "", //本が登録された日付
)