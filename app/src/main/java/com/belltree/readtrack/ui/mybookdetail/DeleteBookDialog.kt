package com.belltree.readtrack.ui.mybookdetail

import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import com.belltree.readtrack.R

/**
 * 本を削除する際に表示するダイアログ
 * @param onDismiss ダイアログを閉じるための関数
 * @param onDelete 本を削除するための関数
 * @param onBack ダイアログを閉じた後に戻るための関数
 */
@Composable
fun DeleteBookDialog(
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit,
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
                    fontWeight = FontWeight.Companion.Bold
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        Toast.makeText(context, R.string.deleteDialog_deleted, Toast.LENGTH_SHORT)
                            .show()
                        onBack()
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