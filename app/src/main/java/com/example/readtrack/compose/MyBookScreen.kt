package com.example.readtrack.compose

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.readtrack.ReadTrackScreen
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
    // TODO: 全体的に使用感が良くないのでUIの見直しを行う
    LaunchedEffect(bookId) {
        savedBooksViewModel.fetchBookDetails(bookId)
    }
    val selectedBook by savedBooksViewModel.selectedBook.collectAsState()
    val currentDateTime = LocalDateTime.now()
    val formattedDate = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm"))
    selectedBook?.let { book ->
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var readPagesCount by remember { mutableStateOf(book.readpage) }
            var comment by remember { mutableStateOf(book.comment) }
            var selectedOption by remember {
                mutableStateOf(
                    when (book.progress) {
                        0 -> "未読"
                        1 -> "読書中"
                        else -> "読了"
                    }
                )
            }
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
                //状態の変更、読了ページ数の変更、感想の記入をここで行う
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "読書状態",
                        fontSize = 16.sp,
                    )
                    EditBookState(
                        selectedOption = selectedOption,
                        onSelectionChange = { newSelection ->
                            selectedOption = newSelection
                        }
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("読了ページ数")
                    OutlinedTextField(
                        value = readPagesCount.toString(),
                        onValueChange = { newValue ->
                            readPagesCount = try {
                                newValue.toInt()
                            } catch (e: NumberFormatException) {
                                0
                            }
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
                Text("感想")
                OutlinedTextField(
                    value = comment.toString(),
                    onValueChange = { comment = it },
                    modifier = Modifier.padding(8.dp)
                )
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
                                readpage = readPagesCount,
                                comment = comment,
                                updatedDate = formattedDate
                            )
                        )
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("変更を保存する")
                }
                Button(
                    onClick = {
                        //ここでライブラリから削除
                        navController.navigate(ReadTrackScreen.Library.name)
                        savedBooksViewModel.deleteBook(book)
                        Toast.makeText(context, "ライブラリから削除しました", Toast.LENGTH_SHORT)
                            .show()
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("ライブラリから削除する")
                }
            }

        }
    } ?: Text("Book not found")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookState(
    selectedOption: String,
    onSelectionChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("未読", "読書中", "読了")
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.padding(8.dp)
    ) {
        val intersectionSource = remember { MutableInteractionSource() }
        TextField(
            value = selectedOption,
            onValueChange = { newSelection ->
                onSelectionChange(newSelection)
            },
            interactionSource = intersectionSource,
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .clickable { expanded = true }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onSelectionChange(option)
                        expanded = false
                    },
                    text = {
                        Text(option)
                    })
            }
        }
    }
}

