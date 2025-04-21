package com.belltree.readtrack.compose.setting

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.belltree.readtrack.R

/**
 * アプリの使用方法を説明するダイアログ
 * @param onDismiss ダイアログを閉じるための関数
 */
@Composable
fun HowToUseDialog(onDismiss: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            when (currentPage) {
                0 -> Text(text = stringResource(id = R.string.setting_explain_first_title))
                1 -> Text(text = stringResource(id = R.string.setting_explain_second_title))
                2 -> Text(text = stringResource(id = R.string.setting_explain_third_title))
            }
        },
        text = {
            when (currentPage) {
                0 -> Text(
                    text = stringResource(id = R.string.setting_explain_first)
                )

                1 -> Text(
                    text = stringResource(id = R.string.setting_explain_second)
                )

                2 -> Text(
                    text = stringResource(id = R.string.setting_explain_third)
                )
            }
        },
        confirmButton = {
            Row {
                if (currentPage > 0) {
                    TextButton(onClick = { currentPage-- }) {
                        Text(text = stringResource(id = R.string.setting_explain_back))
                    }
                }
                if (currentPage < 2) {
                    TextButton(onClick = { currentPage++ }) {
                        Text(text = stringResource(id = R.string.setting_explain_next))
                    }
                } else {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(id = R.string.setting_explain_finish))
                    }
                }
            }
        }
    )
}
