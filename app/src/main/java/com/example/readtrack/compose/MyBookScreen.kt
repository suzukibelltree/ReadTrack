package com.example.readtrack.compose

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.readtrack.R
import com.example.readtrack.Route
import com.example.readtrack.network.BookData
import com.example.readtrack.room.ReadLog
import com.example.readtrack.room.ReadLogsViewModel
import com.example.readtrack.room.SavedBooksViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 自分が登録した本の詳細を表示する画面
 * @param bookId 本のID
 * @param savedBooksViewModel 保存された本のViewModel
 * @param readLogsViewModel 読書ログのViewModel
 * @param navController ナビゲーションコントローラー
 */
@Composable
fun MyBookScreen(
    bookId: String,
    savedBooksViewModel: SavedBooksViewModel,
    readLogsViewModel: ReadLogsViewModel,
    navController: NavController
) {
    LaunchedEffect(bookId) {
        savedBooksViewModel.fetchBookDetails(bookId)
    }
    val selectedBook by savedBooksViewModel.selectedBook.collectAsState()
    val readLogs = readLogsViewModel.allLogs.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val currentDateTime = LocalDateTime.now()
    val currentYearMonthId = DateTimeFormatter.ofPattern("yyyyMM").format(currentDateTime).toInt()
    val formattedDate = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm"))
    // 現在の月に一致するReadLogのインスタンスのみを取得
    val currentMonthLog = readLogs.value.find { it.yearMonthId == currentYearMonthId }
    selectedBook?.let { book ->
        var selectedOption by remember {
            mutableStateOf(
                when (book.progress) {
                    0 -> "未読"
                    1 -> "読書中"
                    else -> "読了"
                }
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ユーザーのページ数入力を受け付ける
            var readPagesCount by remember { mutableStateOf(book.readpage.toString()) }
            var comment by remember { mutableStateOf(book.comment) }
            val context = LocalContext.current
            // ページ数の差分(変更前後)を保持
            var pagesReadDiff by remember { mutableIntStateOf(0) }
            Row {
                AsyncImage(
                    model = book.thumbnail,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .padding(16.dp)
                )
                Column {
                    Text(
                        text = book.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = book.author,
                    )
                    Text(
                        text = "出版日:${book.publishedDate.toString()}",
                    )
                    Text(
                        text = "ライブラリに追加した日: ${book.registeredDate}"
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "読書状態",
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                //状態の変更、読了ページ数の変更、メモの記入をここで行う
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ReadStateCard(
                        book = book,
                        progress = 0,
                        icon = R.drawable.frame,
                        contentDescription = "未読",
                        onProgressChange = {
                            selectedOption = "未読"
                            book.progress = 0
                            readPagesCount = "0"
                        })
                    ReadStateCard(
                        book = book,
                        progress = 1,
                        icon = R.drawable.reading,
                        contentDescription = "読書中",
                        onProgressChange = {
                            selectedOption = "読書中"
                            book.progress = 1
                        }
                    )
                    ReadStateCard(
                        book = book,
                        progress = 2,
                        icon = R.drawable.finished,
                        contentDescription = "読了",
                        onProgressChange = {
                            selectedOption = "読了"
                            book.progress = 2
                            readPagesCount = book.pageCount.toString()
                        }
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("読了ページ数")
                    OutlinedTextField(
                        value = readPagesCount,
                        onValueChange = { newValue ->
                            // 空の文字列になるときにアプリがクラッシュするのを防ぐ
                            if (newValue.isEmpty()) {
                                readPagesCount = ""
                            } else if (newValue.all { it.isDigit() }) { //無効な数字が入力された場合は処理しない
                                readPagesCount = newValue
                                // 読了ページ数が増加したら差分を計算
                                if (newValue.toInt() > book.readpage!!) {
                                    pagesReadDiff = newValue.toInt() - book.readpage!!
                                }
                                // 読了ページ数がページ数を超える場合はページ数に合わせる
                                if (newValue.toInt() > book.pageCount!!) {
                                    readPagesCount = book.pageCount.toString()
                                }
                            }
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f),
                        singleLine = true,
                        // 状態が読書中の場合のみ読了ページ数を変更できるようにする
                        readOnly = (selectedOption != "読書中"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Text(
                        text = "/${book.pageCount}ページ",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Text(
                    text = "メモ",
                    modifier = Modifier.align(Alignment.Start)
                )
                OutlinedTextField(
                    value = comment.toString(),
                    onValueChange = { comment = it },
                    modifier = Modifier.padding(8.dp)
                )
                //TODO: 保存する情報を感想ではなくメモにするか検討
                Button(
                    onClick = {
                        // 保存ボタンが押されたときにトーストで通知
                        Toast.makeText(context, "変更を保存しました", Toast.LENGTH_SHORT).show()
                        //ここで変更された本の情報を保存
                        savedBooksViewModel.updateBook(
                            book.copy(
                                progress = when (selectedOption) {
                                    "未読" -> 0
                                    "読書中" -> 1
                                    else -> 2
                                },
                                readpage = readPagesCount.toInt(),
                                comment = comment,
                                updatedDate = formattedDate
                            )
                        )
                        // 読了ページ数の差分がある場合は読書記録を更新
                        readLogsViewModel.upsertLogInViewModelScope(
                            currentMonthLog?.copy(
                                yearMonthId = currentYearMonthId,
                                readPages = currentMonthLog.readPages + pagesReadDiff
                            ) ?: ReadLog(
                                yearMonthId = currentYearMonthId,
                                readPages = pagesReadDiff
                            )
                        )
                        navController.navigate(Route.Library)
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("変更を保存する")
                }
                Button(
                    onClick = {
                        showDialog = true
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(Color.Black)
                ) {
                    Text("ライブラリから削除する")
                }
            }

        }
        if (showDialog) {
            DeleteBookDialog(
                navController = navController,
                savedBooksViewModel = savedBooksViewModel,
                book = book
            )
        }
    } ?: Text("Book not found")
}

/**
 * 本の読書状況(3状態)を表示するカード
 * @param book 本の情報
 * @param progress 本の読書状況
 * @param icon アイコン
 * @param contentDescription アイコンの説明
 */
@Composable
fun ReadStateCard(
    book: BookData,
    progress: Int,
    @DrawableRes icon: Int,
    contentDescription: String,
    onProgressChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .size(100.dp)
            .clickable { onProgressChange(progress) }
            .padding(8.dp)
            .border(
                width = 2.dp,
                color = if (
                    book.progress == progress
                ) Color.Blue else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = when (progress) {
                0 -> "未読"
                1 -> "読書中"
                else -> "読了"
            },
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

