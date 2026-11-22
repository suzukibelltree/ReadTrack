package com.belltree.readtrack.ui.mybookdetail

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.belltree.readtrack.R

/**
 * 本の読書状況(3状態)を表示するカード
 *
 * 3枚で1つのラジオグループとして振る舞う。呼び出し側の親に `Modifier.selectableGroup()` を付けること。
 * 選択状態は色だけでなく枠線の太さでも区別し、TalkBack には `Role.RadioButton` + selected で伝える。
 * @param progress 本の読書状況([ReadProgress])
 * @param selected このカードが選択されているか
 * @param icon アイコンのリソースID
 * @param onProgressChange 状態変更時の処理
 */
@Composable
fun ReadStateCard(
    progress: Int,
    selected: Boolean,
    @DrawableRes icon: Int,
    onProgressChange: () -> Unit
) {
    Column(
        modifier = Modifier
            .size(100.dp)
            // selectable は子孫のセマンティクスをマージするため、Image + Text が1要素として読み上げられる
            .selectable(
                selected = selected,
                onClick = onProgressChange,
                role = Role.RadioButton,
            )
            .padding(8.dp)
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) Color.Blue else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Image(
            painter = painterResource(id = icon),
            // 状態名は下の Text で読み上げられるため、アイコンは装飾扱いにして二重読み上げを防ぐ
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = when (progress) {
                ReadProgress.UNREAD -> stringResource(R.string.read_state_unread)
                ReadProgress.READING -> stringResource(R.string.read_state_reading)
                else -> stringResource(R.string.read_state_read)
            },
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
