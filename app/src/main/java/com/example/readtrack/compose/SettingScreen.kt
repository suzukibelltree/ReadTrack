package com.example.readtrack.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.readtrack.R
import com.example.readtrack.Route
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingScreen(
    navController: NavController
) {
    // TODO: 設定画面の実装
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column {
            var sizeMenuExpand by remember { mutableStateOf(false) }
            Row {
                Icon(
                    imageVector = if (sizeMenuExpand) Icons.Default.Remove else Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { sizeMenuExpand = !sizeMenuExpand }
                )
                Text(
                    text = stringResource(R.string.setting_character_size),
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
            AnimatedVisibility(sizeMenuExpand) {
                val radiooptions1 = listOf("小", "中", "大")
                val (selectedOption, onOptionSelected) = remember { mutableStateOf(radiooptions1[0]) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp), // 選択肢の間にスペースを追加
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    radiooptions1.forEach { text ->
                        Row(
                            Modifier
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = { onOptionSelected(text) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = null // null recommended for accessibility with screen readers
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        }
        HorizontalDivider()
        Column {
            var themeMenuExpand by remember { mutableStateOf(false) }
            Row {
                Icon(
                    imageVector = if (themeMenuExpand) Icons.Default.Remove else Icons.Default.Add,
                    contentDescription = "Open Menu",
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { themeMenuExpand = !themeMenuExpand }
                )
                Text(
                    text = stringResource(R.string.setting_theme_color),
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
            AnimatedVisibility(themeMenuExpand) {
                val radiooptions2 = listOf("青", "赤", "緑")
                val (selectedOption, onOptionSelected) = remember { mutableStateOf(radiooptions2[0]) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp), // 選択肢の間にスペースを追加
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    radiooptions2.forEach { text ->
                        Row(
                            Modifier
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = { onOptionSelected(text) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = null // null recommended for accessibility with screen readers
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }

                }
            }
        }
        HorizontalDivider()
        Column {
            var switchDarkExpand by remember { mutableStateOf(false) }
            Row {
                Icon(
                    imageVector = if (switchDarkExpand) Icons.Default.Remove else Icons.Default.Add,
                    contentDescription = "Open Menu",
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { switchDarkExpand = !switchDarkExpand }
                )
                Text(
                    text = stringResource(R.string.setting_switch_dark_light),
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
            AnimatedVisibility(switchDarkExpand) {
                val radiooptions3 = listOf("ダーク", "ライト")
                val (selectedOption, onOptionSelected) = remember { mutableStateOf(radiooptions3[0]) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp), // 選択肢の間にスペースを追加
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    radiooptions3.forEach { text ->
                        Row(
                            Modifier
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = { onOptionSelected(text) },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = null // null recommended for accessibility with screen readers
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }

                }
            }
        }
        HorizontalDivider()
        Text(
            text = "ログアウト",
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    FirebaseAuth
                        .getInstance()
                        .signOut()
                    navController.navigate(Route.Login)
                }
        )
        HorizontalDivider()
    }
}
