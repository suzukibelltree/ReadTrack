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
import com.example.readtrack.network.BookData
import com.example.readtrack.room.SavedBooksViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    var showDialog by remember { mutableStateOf(false) }
    val currentDateTime = LocalDateTime.now()
    val formattedDate = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm"))
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
            var readPagesCount by remember { mutableStateOf(book.readpage.toString()) }
            var comment by remember { mutableStateOf(book.comment) }
            val context = LocalContext.current
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
                        book,
                        0,
                        com.example.readtrack.R.drawable.frame,
                        "未読",
                        onProgressChange = {
                            selectedOption = "未読"
                            book.progress = 0
                        })
                    ReadStateCard(
                        book,
                        1,
                        com.example.readtrack.R.drawable.reading,
                        "読書中",
                        onProgressChange = {
                            selectedOption = "読書中"
                            book.progress = 1
                        })
                    ReadStateCard(
                        book,
                        2,
                        com.example.readtrack.R.drawable.finished,
                        "読了",
                        onProgressChange = {
                            selectedOption = "読了"
                            book.progress = 2
                        })
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("読了ページ数")
                    OutlinedTextField(
                        value = readPagesCount,
                        onValueChange = { newValue ->
                            readPagesCount = newValue
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f),
                        singleLine = true,
                        readOnly = selectedOption != "読書中",
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
                        Toast.makeText(context, "変更を保存しました", Toast.LENGTH_SHORT).show()
                        //ここで変更を保存
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

