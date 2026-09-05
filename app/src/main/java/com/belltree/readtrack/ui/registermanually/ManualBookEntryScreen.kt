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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.belltree.readtrack.R
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
                    Toast.makeText(
                        context,
                        R.string.registerManually_cameraPermissionDenied,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is ManualBookUiEvent.ThumbnailSelectionCanceled -> {
                    Toast.makeText(
                        context,
                        R.string.registerManually_captureCanceled,
                        Toast.LENGTH_SHORT
                    ).show()
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
            text = stringResource(R.string.registerManually_enterBookInfo),
            modifier = Modifier.Companion.padding(vertical = 16.dp)
        )

        LabeledTextField(
            value = formState.title,
            onValueChange = viewModel::updateTitle,
            label = stringResource(R.string.registerManually_title_label),
            isError = formState.title.isBlank()
        )

        LabeledTextField(
            value = formState.author,
            onValueChange = viewModel::updateAuthor,
            label = stringResource(R.string.registerManually_author_label)
        )

        LabeledTextField(
            value = formState.publisher,
            onValueChange = viewModel::updatePublisher,
            label = stringResource(R.string.registerManually_publisher_label)
        )

        LabeledTextField(
            value = formState.publishedDate,
            onValueChange = viewModel::updatePublishedDate,
            label = stringResource(R.string.registerManually_publishedDate_label),
            placeholder = stringResource(R.string.registerManually_publishedDate_placeholder)
        )

        LabeledTextField(
            value = formState.pageCount,
            onValueChange = viewModel::updatePageCount,
            label = stringResource(R.string.registerManually_pageCount_label),
            placeholder = stringResource(R.string.registerManually_pageCount_placeholder),
            isNumber = true
        )

        Text(
            stringResource(R.string.registerManually_captureThumbnail_title),
            modifier = Modifier.Companion.padding(vertical = 8.dp)
        )

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
            Text(stringResource(R.string.registerManually_captureButton))
        }

        formState.thumbnail?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = stringResource(R.string.registerManually_capturedThumbnail_description),
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
            Text(stringResource(R.string.registerManually_saveButton))
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