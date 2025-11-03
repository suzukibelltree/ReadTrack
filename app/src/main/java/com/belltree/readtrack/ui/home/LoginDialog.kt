package com.belltree.readtrack.ui.home

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.belltree.readtrack.domain.model.LoginMessageResult

@Composable
fun LoginMessageDialog(result: LoginMessageResult, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = result.message, style = MaterialTheme.typography.titleLarge) },
        text = { Text("これはテスト") },// 本来はここにアニメーションを表示する
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
