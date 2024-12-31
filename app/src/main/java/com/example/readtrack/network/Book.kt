package com.example.readtrack.network

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Google Books APIから取得したデータを持つデータクラス
 * @param items 本のリスト
 */
data class BookLists(
    val items: List<BookItem>?
)

/**
 * 本の情報を持つデータクラス
 * @param id 本のID
 * @param selfLink 本のリンク
 * @param volumeInfo 本の情報
 */
data class BookItem(
    val id: String,
    val selfLink: String,
    val volumeInfo: VolumeInfo
)

/**
 * BookItemのメンバであるvolumeInfoのデータクラス
 * @param title 本のタイトル
 * @param authors 著者
 * @param publisher 出版社
 * @param publishedDate 出版日
 * @param description 本の説明
 * @param imageLinks 本の画像リンク
 * @param pageCount ページ数
 * @param categories カテゴリ
 */
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

/**
 * VolumeInfoのメンバであるimageLinksのデータクラス
 * @param smallThumbnail 本のサムネイル画像リンク
 * @param thumbnail 本のサムネイル画像リンク
 */
data class ImageLinks(
    val smallThumbnail: String,
    val thumbnail: String
)

/**
 * データベースに保存するためのデータクラス
 * BookItemはネストされたデータクラスであるため、Roomでのエラーを回避するために分離する
 * @param id 本のID
 * @param title 本のタイトル
 * @param author 著者
 * @param publisher 出版社
 * @param publishedDate 出版日
 * @param description 本の説明
 * @param thumbnail 本の画像リンク
 * @param pageCount ページ数
 * @param readpage 読んだページ数
 * @param comment 本に対するコメント、感想
 * @param progress 本の状態(0:未読,1:読書中,2:読了)
 * @param registeredDate 本が登録された日付
 * @param updatedDate 本の情報が更新された日付
 */
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
    var updatedDate: String = "" //本の情報が更新された日付
)