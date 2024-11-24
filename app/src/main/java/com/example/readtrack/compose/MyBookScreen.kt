import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.readtrack.network.BookData
import com.example.readtrack.room.SavedBooksViewModel

@Composable
fun MyBookScreen(bookId: String, savedBooksViewModel: SavedBooksViewModel) {
    LaunchedEffect(bookId) {
        savedBooksViewModel.fetchBookDetails(bookId)
    }
    val selectedBook by savedBooksViewModel.selectedBook.collectAsState()
    selectedBook?.let { book ->
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var readpage by remember { mutableStateOf(book.readpage) }
            var comment by remember { mutableStateOf(book.comment) }
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
                    EditBookState(book = book)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("読了ページ数")
                    OutlinedTextField(
                        value = readpage.toString(),
                        onValueChange = { readpage = it.toInt() },
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f),
                        singleLine = true,
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
                    onClick = { /*ここでupdate*/ },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("変更を保存する")
                }
                Button(
                    onClick = { /*ここでdelete*/ },
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
    book: BookData,
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("未読", "読書中", "読了")
    var selectedoption by remember {
        mutableStateOf(
            when (book.progress) {
                0 -> "未読"
                1 -> "読書中"
                else -> "読了"
            }
        )
    }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.padding(8.dp)
    ) {
        val intersectionSource = remember { MutableInteractionSource() }
        TextField(
            value = selectedoption,
            onValueChange = { selectedoption = it },
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
                        selectedoption = option
                        expanded = false
                    },
                    text = {
                        Text(option)
                    })
            }
        }
    }
}

