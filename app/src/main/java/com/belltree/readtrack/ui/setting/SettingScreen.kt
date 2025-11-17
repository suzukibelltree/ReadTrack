package com.belltree.readtrack.ui.setting

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
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.work.WorkManager
import com.belltree.readtrack.R
import com.belltree.readtrack.core.notification.scheduleBookUpdateCheck
import com.belltree.readtrack.data.local.datastore.getValue
import com.belltree.readtrack.data.local.datastore.saveValue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SettingScreen(
    navController: NavController,
    viewmodel: SettingViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var showExplainDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val enableNotifications by viewmodel.enableNotification.collectAsState()
    var selectedThemeColor by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        scope.launch {
            selectedThemeColor = getValue(context, "theme_color").first()
        }
        viewmodel.scheduleEvent.collect { shouldEnable ->
            if (shouldEnable) {
                scheduleBookUpdateCheck(context)
            } else {
                WorkManager.getInstance(context)
                    .cancelUniqueWork("book_update_check")
            }
        }
    }
    Column(
        modifier = Modifier.Companion.fillMaxSize(),
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
    ) {
        Column {
            var themeMenuExpand by remember { mutableStateOf(false) }
            Text(
                text = stringResource(R.string.setting_theme_color),
                fontSize = 20.sp,
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { themeMenuExpand = !themeMenuExpand }
            )
            AnimatedVisibility(themeMenuExpand) {
                val colorOptions = listOf(
                    stringResource(id = R.string.setting_theme_color_red),
                    stringResource(id = R.string.setting_theme_color_blue),
                    stringResource(id = R.string.setting_theme_color_green)
                )
                val (selectedOption, onOptionSelected) = remember {
                    mutableStateOf(
                        selectedThemeColor
                    )
                }
                Row(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Companion.CenterVertically
                ) {
                    colorOptions.forEach { text ->
                        Row(
                            Modifier.Companion
                                .selectable(
                                    selected = (text == selectedThemeColor),
                                    onClick = {
                                        onOptionSelected(text)
                                        selectedThemeColor = text
                                    },
                                    role = Role.Companion.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.Companion.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = null
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.Companion.padding(start = 16.dp)
                            )
                        }
                    }

                }
            }
        }
        HorizontalDivider()
        Text(
            text = stringResource(id = R.string.setting_how_to_use),
            fontSize = 20.sp,
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    showExplainDialog = true
                }
        )
        HorizontalDivider()
        Column {
            var notificationMenuExpand by remember { mutableStateOf(false) }
            Text(
                text = "通知機能の設定",
                fontSize = 20.sp,
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        notificationMenuExpand = !notificationMenuExpand
                    }
            )
            AnimatedVisibility(notificationMenuExpand) {
                Row {
                    Text(
                        text = "通知を有効にする",
                        fontSize = 16.sp,
                        modifier = Modifier.Companion
                            .weight(1f)
                            .padding(16.dp)
                    )
                    Switch(
                        checked = enableNotifications,
                        onCheckedChange = {
                            viewmodel.setNotificationEnabled(it)
                        },
                        modifier = Modifier.Companion.padding(end = 16.dp)
                    )
                }
            }
        }
        HorizontalDivider()
        Spacer(modifier = Modifier.Companion.weight(1f))
        Button(
            onClick = {
                scope.launch {
                    saveValue(
                        context = context,
                        key = "theme_color",
                        value = selectedThemeColor
                    )
                    viewmodel.applySettings()
                }
                Toast.makeText(context, R.string.setting_save_complete, Toast.LENGTH_SHORT).show()
            }
        ) {
            Text(text = stringResource(id = R.string.setting_save))
        }
        Spacer(modifier = Modifier.Companion.weight(1f))
    }
    if (showExplainDialog) {
        HowToUseDialog { showExplainDialog = false }
    }
}