package com.belltree.readtrack.compose.myBookDetail

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.belltree.readtrack.R
import com.belltree.readtrack.Route
import com.belltree.readtrack.datastore.saveValue
import com.belltree.readtrack.room.ReadLog
import com.belltree.readtrack.ui.theme.SaveButtonColor
import com.belltree.readtrack.utils.getCurrentFormattedTime
import com.belltree.readtrack.utils.getCurrentYearMonthAsInt
import kotlinx.coroutines.launch

/**
 * 自分が登録した本の詳細を表示する画面
 * @param bookId 本のID
 * @param myBookDetailViewModel 保存された本のViewModel
 * @param navController ナビゲーションコントローラー
 */
@Composable
fun MyBookScreen(
    bookId: String,
    myBookDetailViewModel: MyBookDetailViewModel = hiltViewModel(),
    navController: NavController
) {
    LaunchedEffect(bookId) {
        myBookDetailViewModel.setBookId(bookId)
    }
    val uiState = myBookDetailViewModel.uiState.collectAsStateWithLifecycle()
    val showCompleteDialog = myBookDetailViewModel.showCompleteDialog.collectAsStateWithLifecycle()
    when (val state = uiState.value) {
        is MyBookDetailUiState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
        }

        is MyBookDetailUiState.Success -> {
            val model = state.bindingModel
            val selectedBook = model.myBookDetailBookBindingModel
            val selectedBookReadLog = model.readLog
            val scope = rememberCoroutineScope()
            var showDeleteDialog by remember { mutableStateOf(false) }
            val currentYearMonthId = getCurrentYearMonthAsInt()
            val formattedDate = getCurrentFormattedTime()
            selectedBook.let { book ->
                var selectedOption by remember {
                    mutableIntStateOf(
                        when (book.progress) {
                            0 -> R.string.read_state_unread
                            1 -> R.string.read_state_reading
                            else -> R.string.read_state_read
                        }
                    )
                }
                // ユーザーのページ数入力を受け付ける
                var readPagesCount by remember { mutableStateOf(book.readPages.toString()) }
                var comment by remember { mutableStateOf(book.comment) }
                val context = LocalContext.current
                // ページ数の差分(変更前後)を保持
                var pagesReadDiff by remember { mutableIntStateOf(0) }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (book.thumbnail != null) {
                                AsyncImage(
                                    model = book.thumbnail,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(R.drawable.unknown),
                                    contentDescription = "thumbnail not found",
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f) // 余ったスペースを利用する
                                    .padding(start = 16.dp) // 画像との間隔をあける
                            ) {
                                Text(
                                    text = book.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = book.authors!!,
                                )
                                Text(
                                    text = stringResource(
                                        R.string.myBook_addedDate,
                                        book.registeredDate
                                    ),
                                )
                                if (book.progress != 0) {
                                    Text(
                                        text = stringResource(
                                            R.string.myBook_updatedDate,
                                            book.updatedDate
                                        ),
                                    )
                                }
                            }
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.myBook_readState),
                                fontSize = 16.sp,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            //状態の変更を行うアイコン3つ
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ReadStateCard(
                                    progress = ReadProgress.UNREAD,
                                    icon = R.drawable.frame,
                                    contentDescription = stringResource(R.string.read_state_unread),
                                    selectedOption = selectedOption,
                                    onProgressChange = {
                                        selectedOption = R.string.read_state_unread
                                        readPagesCount = "0"
                                    })
                                ReadStateCard(
                                    progress = ReadProgress.READING,
                                    icon = R.drawable.reading,
                                    contentDescription = stringResource(R.string.read_state_reading),
                                    selectedOption = selectedOption,
                                    onProgressChange = {
                                        selectedOption = R.string.read_state_reading
                                    }
                                )
                                ReadStateCard(
                                    progress = ReadProgress.READ,
                                    icon = R.drawable.finished,
                                    contentDescription = stringResource(R.string.read_state_read),
                                    selectedOption = selectedOption,
                                    onProgressChange = {
                                        selectedOption = R.string.read_state_read
                                        readPagesCount = book.pageCount.toString()
                                    }
                                )
                            }
                        }
                    }
                    // APIがページ数の情報を持っている場合のみ表示
                    if (book.pageCount != 0 && book.pageCount != null) {
                        // 読了ページ数の入力欄
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
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
                                            pagesReadDiff = newValue.toInt() - book.readPages!!
                                            // 読了ページ数がページ数を超える場合はページ数に合わせる
                                            if (newValue.toInt() > book.pageCount) {
                                                readPagesCount = book.pageCount.toString()
                                                selectedOption = R.string.read_state_read
                                                pagesReadDiff = book.pageCount - book.readPages
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
                                    text = stringResource(
                                        R.string.myBook_pageCount,
                                        book.pageCount
                                    ),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }
                        // 読書記録(履歴)の表示
                        item {
                            val itemCount = selectedBookReadLog.size
                            val itemHeight = 40.dp  // 1つのログアイテムのだいたいの高さ
                            val maxHeight = 160.dp  // 表示高さの上限

                            val calculatedHeight = (itemCount * itemHeight).coerceAtMost(maxHeight)
                            LazyColumn(
                                modifier = Modifier
                                    .height(calculatedHeight)
                                    .fillMaxWidth()
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {

                                items(itemCount) { index ->
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val log = selectedBookReadLog[index]
                                        Row(
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = stringResource(
                                                    R.string.myBook_readLogDate,
                                                    log.recordedAt
                                                ),
                                                fontSize = 16.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp)
                                            )
                                            Text(
                                                text = stringResource(
                                                    R.string.myBook_readLogPages,
                                                    log.readPages
                                                ),
                                                fontSize = 16.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.myBook_comment),
                                modifier = Modifier.align(Alignment.Start)
                            )
                            OutlinedTextField(
                                value = comment.toString(),
                                onValueChange = { comment = it },
                                modifier = Modifier.padding(8.dp)
                            )
                            Button(
                                onClick = {
                                    // 読了ページ数が空の場合はトーストで通知
                                    if (readPagesCount == "") {
                                        Toast.makeText(
                                            context,
                                            R.string.myBook_page_isnull,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        // 保存されているものよりも少ない読了ページ数で保存することはできない(トーストで通知)
                                    } else if (pagesReadDiff < 0) {
                                        Toast.makeText(
                                            context,
                                            R.string.myBook_page_cannot_decrease,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        // 保存ボタンが押されたときにトーストで通知
                                        Toast.makeText(
                                            context,
                                            R.string.myBook_Save_complete,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        myBookDetailViewModel.openCompleteDialog()
                                        //ここで変更された本の情報を保存
                                        myBookDetailViewModel.updateBook(
                                            progress = when (selectedOption) {
                                                R.string.read_state_unread -> ReadProgress.UNREAD
                                                R.string.read_state_reading -> ReadProgress.READING
                                                else -> ReadProgress.READ
                                            },
                                            readPages = readPagesCount.toInt(),
                                            comment = comment,
                                            updatedDate = formattedDate

                                        )
                                        // 読了ページ数の差分がある場合は読書記録を更新
                                        myBookDetailViewModel.insertLog(
                                            ReadLog(
                                                yearMonthId = currentYearMonthId,
                                                bookId = bookId,
                                                readPages = pagesReadDiff,
                                                recordedAt = formattedDate
                                            )
                                        )
                                        scope.launch {
                                            saveValue(
                                                context = context,
                                                key = "lastUpdatedDate",
                                                value = formattedDate
                                            )
                                        }
                                        //navController.navigate(Route.Library)
                                    }
                                },
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(SaveButtonColor)
                            ) {
                                Text(text = stringResource(R.string.myBook_Save))
                            }
                            Button(
                                onClick = {
                                    showDeleteDialog = true
                                },
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(Color.Red)
                            ) {
                                Text(text = stringResource(R.string.myBook_delete))
                            }
                        }
                    }
                }
                if (showDeleteDialog) {
                    DeleteBookDialog(
                        onDismiss = {
                            showDeleteDialog = false
                        },
                        onDelete = { myBookDetailViewModel.deleteBook() },
                        onBack = { navController.navigate(Route.Library) }
                    )
                }
                if (showCompleteDialog.value) {
                    CompleteBookDialog(
                        bookThumbnailUrl = book.thumbnail ?: "",
                        onDismissRequest = {
                            myBookDetailViewModel.closeCompleteDialog()
                            navController.navigate(Route.Library)
                        },
                        onPostToX = {
                            //TODO: Xに投稿する処理を実装
                        }
                    )
                }
            }
        }

        is MyBookDetailUiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "エラーが発生しました",
                )
            }
        }
    }
}