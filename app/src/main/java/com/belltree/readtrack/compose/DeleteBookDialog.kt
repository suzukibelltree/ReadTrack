package com.belltree.readtrack.compose

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
import com.belltree.readtrack.room.MyBooksViewModel

/**
 * 本を削除する際に表示するダイアログ
 * @param navController ナビゲーションコントローラー
 * @param myBooksViewModel 保存された本のViewModel
 * @param book 削除する本の情報
 */
@Composable
fun DeleteBookDialog(
    navController: NavController,
    myBooksViewModel: MyBooksViewModel,
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
                        navController.navigate(Route.Library)
                        Toast.makeText(context, R.string.deleteDialog_deleted, Toast.LENGTH_SHORT)
                            .show()
                    }
                ) {
                    Text(text = stringResource(R.string.deleteDialog_positive))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Text(text = stringResource(R.string.deleteDialog_cancel))
                }
            }
        )
    }
}
