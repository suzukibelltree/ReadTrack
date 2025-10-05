package com.belltree.readtrack.ui.mybookdetail

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.belltree.readtrack.R

/**
 * 本の読書状況(3状態)を表示するカード
 * @param progress 本の読書状況
 * @param selectedOption 選択された状態
 * @param icon アイコンのリソースID
 * @param contentDescription アイコンの説明
 * @param onProgressChange 状態変更時の処理
 */
@Composable
fun ReadStateCard(
    progress: Int,
    selectedOption: Int,
    @DrawableRes icon: Int,
    contentDescription: String,
    onProgressChange: () -> Unit
) {
    Column(
        modifier = Modifier.Companion
            .size(100.dp)
            .clickable { onProgressChange() }
            .padding(8.dp)
            .border(
                width = 2.dp,
                color = if (selectedOption == when (progress) {
                        0 -> R.string.read_state_unread
                        1 -> R.string.read_state_reading
                        else -> R.string.read_state_read
                    }
                ) Color.Companion.Blue else Color.Companion.Gray,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            modifier = Modifier.Companion
                .size(48.dp)
                .align(Alignment.Companion.CenterHorizontally)
        )
        Text(
            text = when (progress) {
                0 -> stringResource(R.string.read_state_unread)
                1 -> stringResource(R.string.read_state_reading)
                else -> stringResource(R.string.read_state_read)
            },
            fontSize = 20.sp,
            modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally)
        )
    }
}