package com.belltree.readtrack.ui.search.isbn

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.belltree.readtrack.R
import com.belltree.readtrack.ui.navigation.Route
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

/**
 * バーコードスキャン画面
 * @param isbnSearchViewModel ISBN検索のViewModel
 * @param searchedBookDetailViewModel 検索結果の詳細を表示するViewModel
 * @param navController ナビゲーションコントローラー
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BarcodeScannerScreen(
    isbnSearchViewModel: ISBNSearchViewModel = hiltViewModel(),
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        var showDialog by remember { mutableStateOf(false) }
        val scannedIsbn by isbnSearchViewModel.isbn.collectAsState(initial = "")


        Box(modifier = Modifier.Companion.fillMaxSize()) {
            // カメラ映像（背面）
            BarcodeScannerView { value ->
                // スキャンした値が空でない、かつ、スキャンした値が前回のスキャン結果と異なり、かつ97から始まる(ISBNの条件)場合
                if (!showDialog && value != scannedIsbn && value.startsWith("97")) {
                    isbnSearchViewModel.setIsbn(value)
                    showDialog = true
                }
            }

            Column(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .align(Alignment.Companion.TopCenter)
                    .background(Color.Companion.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.Companion.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.barcode_scanner_direction))
            }
            if (showDialog) {
                ScanResultDialog(
                    isbn = scannedIsbn,
                    onConfirm = {
                        showDialog = false
                        scope.launch {
                            val book = isbnSearchViewModel.searchBookByISBN()
                            if (book == null) {
                                Toast.makeText(
                                    context,
                                    R.string.barcode_scanner_nobook,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                navController.navigate("${Route.BookDetail}/${book.id}") {
                                    popUpTo(Route.BarcodeScanner) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    },
                    onDismiss = {
                        showDialog = false
                    }
                )
            }
        }
    }
}

/**
 * スキャン結果のダイアログ
 * @param isbn スキャンしたISBN
 * @param onConfirm 確認ボタンが押されたときの処理
 * @param onDismiss ダイアログが閉じられたときの処理
 */
@Composable
fun ScanResultDialog(
    isbn: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.barcode_scanner_dialog_message),
                fontSize = 20.sp
            )
        },
        text = { Text("ISBN: $isbn") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.barcode_scanner_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.barcode_scanner_cancel))
            }
        }
    )
}