package com.example.readtrack.compose

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.readtrack.R
import com.example.readtrack.Route
import com.example.readtrack.datastore.getDarkLightTheme
import com.example.readtrack.datastore.getValue
import com.example.readtrack.datastore.saveDarkLightTheme
import com.example.readtrack.datastore.saveValue
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SettingScreen(
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var selectedCharSize by remember { mutableStateOf("") }
        var selectedThemeColor by remember { mutableStateOf("") }
        var isDarkTheme by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            scope.launch {
                selectedCharSize = getValue(context, "character_size").first()
                selectedThemeColor = getValue(context, "theme_color").first()
                isDarkTheme = getDarkLightTheme(context).first()
            }
        }
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
                val (selectedOption, onOptionSelected) = remember { mutableStateOf(selectedCharSize) }
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
                                    selected = (text == selectedCharSize),
                                    onClick = {
                                        onOptionSelected(text)
                                        selectedCharSize = text
                                    },
                                    role = Role.RadioButton,

                                    )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = null
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
                val (selectedOption, onOptionSelected) = remember {
                    mutableStateOf(
                        selectedThemeColor
                    )
                }
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
                                    selected = (text == selectedThemeColor),
                                    onClick = {
                                        onOptionSelected(text)
                                        selectedThemeColor = text
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = null
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
                val (selectedOption, onOptionSelected) = remember { mutableStateOf(if (isDarkTheme) "ダーク" else "ライト") }
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
                                    onClick = {
                                        onOptionSelected(text)
                                        isDarkTheme = (text == "ダーク")
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = null
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
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                scope.launch {
                    if (selectedCharSize != "") {
                        saveValue(
                            context = context,
                            key = "character_size",
                            value = selectedCharSize
                        )
                        saveValue(
                            context = context,
                            key = "theme_color",
                            value = selectedThemeColor
                        )
                        saveDarkLightTheme(
                            context = context,
                            value = isDarkTheme
                        )
                    }
                }
                Toast.makeText(context, "設定を保存しました", Toast.LENGTH_SHORT).show()
            }
        ) {
            Text(text = "この設定を保存する")
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
