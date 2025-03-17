package com.belltree.readtrack.compose

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
import com.belltree.readtrack.R
import com.belltree.readtrack.datastore.getValue
import com.belltree.readtrack.datastore.saveValue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SettingScreen(
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    var showExplainDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var selectedThemeColor by remember { mutableStateOf("") }
        LaunchedEffect(Unit) {
            scope.launch {
                selectedThemeColor = getValue(context, "theme_color").first()
            }
        }
        Column {
            var themeMenuExpand by remember { mutableStateOf(false) }
            Text(
                text = stringResource(R.string.setting_theme_color),
                fontSize = 20.sp,
                modifier = Modifier
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    colorOptions.forEach { text ->
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
        Text(
            text = stringResource(id = R.string.setting_how_to_use),
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    showExplainDialog = true
                }
        )
        HorizontalDivider()
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                scope.launch {
                    saveValue(
                        context = context,
                        key = "theme_color",
                        value = selectedThemeColor
                    )
                }
                Toast.makeText(context, R.string.setting_save_complete, Toast.LENGTH_SHORT).show()
            }
        ) {
            Text(text = stringResource(id = R.string.setting_save))
        }
        Spacer(modifier = Modifier.weight(1f))
    }
    if (showExplainDialog) {
        HowToUseDialog { showExplainDialog = false }
    }
}
