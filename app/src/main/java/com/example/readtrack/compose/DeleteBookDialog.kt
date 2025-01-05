package com.example.readtrack.compose

import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.readtrack.Route
import com.example.readtrack.network.BookData
import com.example.readtrack.room.SavedBooksViewModel

/**
 * 本を削除する際に表示するダイアログ
 * @param navController ナビゲーションコントローラー
 * @param savedBooksViewModel 保存された本のViewModel
 * @param book 削除する本の情報
 */
@Composable
fun DeleteBookDialog(
    navController: NavController,
    savedBooksViewModel: SavedBooksViewModel,
    book: BookData
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = {
            navController.popBackStack()
        }
    ) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text("本を削除しますか？")
            },
            text = {
                Text(
                    text = "本を削除すると、登録された本の情報が全て削除されます。",
                    fontWeight = FontWeight.Bold
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        savedBooksViewModel.deleteBook(book)
                        navController.navigate(Route.Library)
                        Toast.makeText(context, "本を削除しました", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("削除")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Text("キャンセル")
                }
            }
        )
    }
}
