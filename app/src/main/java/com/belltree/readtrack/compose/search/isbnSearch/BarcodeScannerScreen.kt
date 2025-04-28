package com.belltree.readtrack.compose.search.isbnSearch

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.belltree.readtrack.R
import com.belltree.readtrack.Route
import com.belltree.readtrack.compose.search.SearchedBookDetailViewModel
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
    isbnSearchViewModel: ISBNSearchViewModel,
    searchedBookDetailViewModel: SearchedBookDetailViewModel,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        val scannedValue by isbnSearchViewModel.isbn.collectAsState(initial = "")

        Box(modifier = Modifier.fillMaxSize()) {
            // カメラ映像（背面）
            BarcodeScannerView { value ->
                isbnSearchViewModel.setIsbn(value)
            }

            // 下部にスキャン結果と検索ボタンを表示
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.barcode_scan_result, scannedValue))
                Button(
                    onClick = {
                        scope.launch {
                            // ISBNが空でない場合に検索を実行
                            if (scannedValue != "") {
                                val book = isbnSearchViewModel.searchBookByISBN()
                                if (book == null) {
                                    Toast.makeText(
                                        context,
                                        R.string.barcode_scanner_nobook,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    searchedBookDetailViewModel.loadBookById(
                                        bookId = book.id,
                                        sourceBookItem = book
                                    )
                                    // 本の詳細画面に遷移する
                                    navController.navigate(
                                        "${Route.BookDetail}/${book.id}"
                                    ) {
                                        popUpTo(Route.BarcodeScanner) {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(top = 8.dp)
                ) {
                    Text(text = stringResource(R.string.barcode_scanner_button))
                }
            }
        }
    }
}

