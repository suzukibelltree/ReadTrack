package com.belltree.readtrack.ui.registermanually

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.belltree.readtrack.ui.navigation.Route
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ManualBookEntryScreen(
    navController: NavController,
    viewModel: ManualBookEntryViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val context = LocalContext.current
    val eventFlow = viewModel.eventFlow
    val scrollState = rememberScrollState()

    // 撮影した書影画像の一時保存用URI
    var imageUri = remember { createImageUri(context) }

    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is ManualBookUiEvent.CameraPermissionDenied -> {
                    Toast.makeText(context, "カメラの使用が許可されていません", Toast.LENGTH_SHORT)
                        .show()
                }

                is ManualBookUiEvent.ThumbnailSelectionCanceled -> {
                    Toast.makeText(context, "撮影がキャンセルされました", Toast.LENGTH_SHORT).show()
                }

                is ManualBookUiEvent.BookSaved -> {
                    navController.navigate(Route.Library) {
                        popUpTo(Route.RegisterManually) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    // カメラランチャー
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.onThumbnailSelected(imageUri.toString())
        } else {
            // 撮影キャンセル時の処理（例：Snackbarなどに変更可能）
            viewModel.onThumbnailSelectionCanceled()
        }
    }

    // 権限要求ランチャー
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageUri(context)
            imageUri = uri
            cameraLauncher.launch(uri)
        } else {
            viewModel.onCameraPermissionDenied()
        }
    }

    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "書籍情報を入力してください",
            modifier = Modifier.Companion.padding(vertical = 16.dp)
        )

        LabeledTextField(
            value = formState.title,
            onValueChange = viewModel::updateTitle,
            label = "タイトル(入力必須)",
            isError = formState.title.isBlank()
        )

        LabeledTextField(
            value = formState.author,
            onValueChange = viewModel::updateAuthor,
            label = "著者名"
        )

        LabeledTextField(
            value = formState.publisher,
            onValueChange = viewModel::updatePublisher,
            label = "出版社"
        )

        LabeledTextField(
            value = formState.publishedDate,
            onValueChange = viewModel::updatePublishedDate,
            label = "出版日",
            placeholder = "例: 2024-10-01"
        )

        LabeledTextField(
            value = formState.pageCount,
            onValueChange = viewModel::updatePageCount,
            label = "ページ数(入力推奨)",
            placeholder = "例: 300",
            isNumber = true
        )

        Text("書影画像の撮影", modifier = Modifier.Companion.padding(vertical = 8.dp))

        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val uri = createImageUri(context)
                    imageUri = uri
                    cameraLauncher.launch(uri)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            modifier = Modifier.Companion.fillMaxWidth()
        ) {
            Text("書影画像を撮影する")
        }

        formState.thumbnail?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "撮影された書影画像",
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(120.dp)
            )
        }

        Button(
            onClick = {
                viewModel.saveBook {
                    navController.navigate(Route.Library) {
                        popUpTo(Route.RegisterManually) {
                            inclusive = true
                        }
                    }
                }
            },
            enabled = formState.isSaveEnabled,
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("保存")
        }
    }
}

/**
 * 再利用可能なテキスト入力フィールド
 */
@Composable
fun LabeledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    isNumber: Boolean = false,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
        singleLine = true,
        isError = isError,
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        keyboardOptions = if (isNumber) {
            KeyboardOptions.Companion.Default.copy(keyboardType = KeyboardType.Companion.Number)
        } else {
            KeyboardOptions.Companion.Default
        }
    )
}

/**
 * 書影画像を保存する一時URIを生成
 */
private fun createImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val fileName = "book_image_$timeStamp.jpg"
    val storageDir = File(context.getExternalFilesDir(null), "book_images").apply {
        if (!exists()) mkdirs()
    }
    val imageFile = File(storageDir, fileName)

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}