package com.belltree.readtrack.compose.myBooks

import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.belltree.readtrack.R
import com.belltree.readtrack.Route
import com.belltree.readtrack.network.BookData

/**
 * 本を削除する際に表示するダイアログ
 * @param myBooksViewModel 本の情報を取得するためのViewModel
 * @param book 削除する本の情報
 * @param onDismiss ダイアログを閉じるための関数
 * @param navController ナビゲーションコントローラー
 */
@Composable
fun DeleteBookDialog(
    myBooksViewModel: MyBooksViewModel,
    book: BookData,
    onDismiss: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = {
            onDismiss()
        }
    ) {
        AlertDialog(
            onDismissRequest = {
                onDismiss()
            },
            title = {
                Text(text = stringResource(R.string.deleteDialog_question))
            },
            text = {
                Text(
                    text = stringResource(R.string.deleteDialog_alert),
                    fontWeight = FontWeight.Bold
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        myBooksViewModel.deleteBook(book)
                        Toast.makeText(context, R.string.deleteDialog_deleted, Toast.LENGTH_SHORT)
                            .show()
                        navController.navigate(Route.Library)
                    }
                ) {
                    Text(text = stringResource(R.string.deleteDialog_positive))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(R.string.deleteDialog_cancel))
                }
            }
        )
    }
}
