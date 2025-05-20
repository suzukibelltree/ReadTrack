package com.belltree.readtrack.compose.registerManually

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.belltree.readtrack.Route

/**
 * 書籍情報を手動で登録する画面
 * @param navController ナビゲーションコントローラー
 * @param viewModel 書籍情報を登録するViewModel
 */
@Composable
fun ManualBookEntryScreen(
    navController: NavController,
    viewModel: ManualBookEntryViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // PhotoPicker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                viewModel.onThumbnailSelected(it.toString())
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "書籍情報を入力してください",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // タイトルの入力
        TextField(
            value = formState.title,
            onValueChange = { viewModel.updateTitle(it) },
            label = { Text("タイトル(入力必須)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = formState.title.isBlank(),
        )

        // 著者名の入力
        OutlinedTextField(
            value = formState.author,
            onValueChange = { viewModel.updateAuthor(it) },
            label = { Text("著者名") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // 出版社名の入力
        OutlinedTextField(
            value = formState.publisher,
            onValueChange = { viewModel.updatePublisher(it) },
            label = { Text("出版社") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formState.publishedDate,
            onValueChange = { viewModel.updatePublishedDate(it) },
            label = { Text("出版日") },
            placeholder = { Text(text = "例:2024-10-01") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formState.pageCount,
            onValueChange = { viewModel.updatePageCount(it) },
            label = { Text("ページ数(入力推奨)") },
            placeholder = { Text(text = "例:300") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )


        Text(
            text = "書影画像の選択",
            modifier = Modifier.padding(8.dp)
        )
        Button(onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
            Text(text = "画像を選択")
        }

        formState.thumbnail?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "選択された書影画像",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(120.dp),
            )
        }

        Button(
            onClick = {
                viewModel.saveBook {
                    navController.navigate(Route.Library)
                }
            },
            enabled = formState.isSaveEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("保存")
        }
    }
}
