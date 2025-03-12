package com.example.readtrack.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun HowToUseDialog(onDismiss: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            when (currentPage) {
                0 -> Text(text = "1.\uD83D\uDD0D本を検索してライブラリに追加")
                1 -> Text(text = "2.\uD83D\uDCDA読書状態とページ数を記録")
                2 -> Text(text = "3.\uD83D\uDCCA月ごとの読書統計を確認")
            }
        },
        text = {
            when (currentPage) {
                0 -> Text(
                    "本のタイトルを検索し、詳細情報のページから「ライブラリに追加」ボタンを押すと、その本の情報を保存できます。\n" +
                            "追加した本は、いつでもライブラリから確認できます。"
                )

                1 -> Text(
                    "ライブラリにある本は、以下の読書状態を設定できます。\n" +
                            "\n" +
                            "未読 \uD83D\uDCD8（まだ読んでいない）\n" +
                            "読書中 \uD83D\uDCD6（読んでいる途中）\n" +
                            "読了 ✅（読み終わった）\n" +
                            "\nまた、読んだページ数を入力して進捗を記録できます。"
                )

                2 -> Text(
                    "読んだページ数は月ごとに記録され、グラフで確認できます。\n" +
                            "読書の進み具合を振り返りながら、次の本を読む計画を立てましょう！"
                )
            }
        },
        confirmButton = {
            Row {
                if (currentPage > 0) {
                    TextButton(onClick = { currentPage-- }) {
                        Text("戻る")
                    }
                }
                if (currentPage < 2) {
                    TextButton(onClick = { currentPage++ }) {
                        Text("次へ")
                    }
                } else {
                    TextButton(onClick = onDismiss) {
                        Text("閉じる")
                    }
                }
            }
        }
    )
}
