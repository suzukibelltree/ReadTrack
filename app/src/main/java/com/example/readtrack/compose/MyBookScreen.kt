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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.readtrack.R
import com.example.readtrack.Route
import com.example.readtrack.getCurrentFormattedTime
import com.example.readtrack.getCurrentYearMonthAsInt
import com.example.readtrack.room.ReadLog
import com.example.readtrack.room.SavedBooksViewModel

/**
 * 自分が登録した本の詳細を表示する画面
 * @param bookId 本のID
 * @param savedBooksViewModel 保存された本のViewModel
 * @param navController ナビゲーションコントローラー
 */
@Composable
fun MyBookScreen(
    bookId: String,
    savedBooksViewModel: SavedBooksViewModel,
    navController: NavController
) {
    LaunchedEffect(bookId) {
        savedBooksViewModel.fetchBookDetails(bookId)
    }
    val selectedBook by savedBooksViewModel.selectedBook.collectAsState()
    val readLogs = savedBooksViewModel.allLogs.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val currentYearMonthId = getCurrentYearMonthAsInt()
    val formattedDate = getCurrentFormattedTime()
    // 現在の月に一致するReadLogのインスタンスのみを取得
    val currentMonthLog = readLogs.value.find { it.yearMonthId == currentYearMonthId }
    selectedBook?.let { book ->
        var selectedOption by remember {
            mutableIntStateOf(
                when (book.progress) {
                    0 -> R.string.read_state_unread
                    1 -> R.string.read_state_reading
                    else -> R.string.read_state_read
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
                        text = stringResource(R.string.myBook_publishedDate, book.publishedDate!!),
                    )
                    Text(
                        text = stringResource(R.string.myBook_addedDate, book.registeredDate),
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
                    text = stringResource(R.string.myBook_readState),
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
                        progress = 0,
                        icon = R.drawable.frame,
                        contentDescription = stringResource(R.string.read_state_unread),
                        selectedOption = selectedOption,
                        onProgressChange = {
                            selectedOption = R.string.read_state_unread
                            readPagesCount = "0"
                        })
                    ReadStateCard(
                        progress = 1,
                        icon = R.drawable.reading,
                        contentDescription = stringResource(R.string.read_state_reading),
                        selectedOption = selectedOption,
                        onProgressChange = {
                            selectedOption = R.string.read_state_reading
                        }
                    )
                    ReadStateCard(
                        progress = 2,
                        icon = R.drawable.finished,
                        contentDescription = stringResource(R.string.read_state_read),
                        selectedOption = selectedOption,
                        onProgressChange = {
                            selectedOption = R.string.read_state_read
                            readPagesCount = book.pageCount.toString()
                        }
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.myBook_pagesRead))
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
                                    pagesReadDiff = newValue.toInt() - book.readpage
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
                        readOnly = (selectedOption != R.string.read_state_reading),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Text(
                        text = stringResource(R.string.myBook_pageCount, book.pageCount!!),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                //TODO: 保存する情報を感想ではなくメモにするか検討
                Text(
                    text = stringResource(R.string.myBook_memo),
                    modifier = Modifier.align(Alignment.Start)
                )
                OutlinedTextField(
                    value = comment.toString(),
                    onValueChange = { comment = it },
                    modifier = Modifier.padding(8.dp)
                )
                Button(
                    onClick = {
                        // 保存ボタンが押されたときにトーストで通知
                        Toast.makeText(context, R.string.myBook_Save_complete, Toast.LENGTH_SHORT)
                            .show()
                        //ここで変更された本の情報を保存
                        savedBooksViewModel.updateBook(
                            book.copy(
                                progress = when (selectedOption) {
                                    R.string.read_state_unread -> 0
                                    R.string.read_state_reading -> 1
                                    else -> 2
                                },
                                readpage = readPagesCount.toInt(),
                                comment = comment,
                                updatedDate = formattedDate
                            )
                        )
                        // 読了ページ数の差分がある場合は読書記録を更新
                        savedBooksViewModel.upsertLog(
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
                    Text(text = stringResource(R.string.myBook_Save))
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
                    Text(text = stringResource(R.string.myBook_delete))
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
    } ?: Text(text = stringResource(R.string.myBook_book_not_found))
}

/**
 * 本の読書状況(3状態)を表示するカード
 * @param progress 本の読書状況
 * @param icon アイコン
 * @param contentDescription アイコンの説明
 */
@Composable
fun ReadStateCard(
    progress: Int,
    selectedOption: Int,
    @DrawableRes icon: Int,
    contentDescription: String,
    onProgressChange: () -> Unit
) {
    Column(
        modifier = Modifier
            .size(100.dp)
            .clickable { onProgressChange() }
            .padding(8.dp)
            .border(
                width = 2.dp,
                color = if (selectedOption == when (progress) {
                        0 -> R.string.read_state_unread
                        1 -> R.string.read_state_reading
                        else -> R.string.read_state_read
                    }
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
                0 -> stringResource(R.string.read_state_unread)
                1 -> stringResource(R.string.read_state_reading)
                else -> stringResource(R.string.read_state_read)
            },
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

