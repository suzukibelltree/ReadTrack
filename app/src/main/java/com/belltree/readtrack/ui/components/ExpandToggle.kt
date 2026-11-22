package com.belltree.readtrack.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.belltree.readtrack.R

/**
 * タップで開閉する要素(設定項目の見出し、「もっと見る」など)用の Modifier
 * TalkBack に「ボタン」であることと、現在の開閉状態(展開中/折りたたみ中)を伝える
 * @param expanded 展開中かどうか
 * @param onToggle タップ時の開閉処理
 */
@Composable
fun Modifier.expandToggle(
    expanded: Boolean,
    onToggle: () -> Unit,
): Modifier {
    val stateDescription = stringResource(
        if (expanded) R.string.a11y_expanded else R.string.a11y_collapsed
    )
    return this
        .clickable(role = Role.Button, onClick = onToggle)
        .semantics { this.stateDescription = stateDescription }
}
