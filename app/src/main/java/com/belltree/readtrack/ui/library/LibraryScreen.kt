package com.belltree.readtrack.ui.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.belltree.readtrack.R
import com.belltree.readtrack.themecolor.AppColors
import com.belltree.readtrack.ui.navigation.Route
import kotlinx.coroutines.launch

/**
 * ライブラリ画面
 * 登録した本の一覧をここで表示する
 * @param navController ナビゲーションコントローラー
 * @param libraryViewModel 保存された本のViewModel
 */
@Composable
fun LibraryScreen(
    navController: NavController,
    libraryViewModel: LibraryViewModel = hiltViewModel(),
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val uiState = libraryViewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(initialPage = 0) { 3 }
    val scope = rememberCoroutineScope()
    val textColor = AppColors.textColor
    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            contentColor = Color.Companion.Blue,
            modifier = Modifier.Companion.fillMaxWidth()
        ) {
            Tab(
                selected = pagerState.currentPage == 0,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                text = {
                    Text(
                        text = stringResource(R.string.read_state_unread),
                        color = textColor
                    )
                }
            )
            Tab(
                selected = pagerState.currentPage == 1,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
                text = {
                    Text(
                        text = stringResource(R.string.read_state_reading),
                        color = textColor
                    )
                }
            )
            Tab(
                selected = pagerState.currentPage == 2,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                },
                text = {
                    Text(
                        text = stringResource(R.string.read_state_read),
                        color = textColor
                    )
                }
            )
        }
        when (val state = uiState.value) {
            is LibraryUiState.Loading -> {
                Column(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Companion.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is LibraryUiState.Success -> {
                val savedBooks = state.bindingModel.libraryBookBindingModel
                HorizontalPager(state = pagerState) { page ->
                    Box(Modifier.fillMaxSize()) {
                        val filteredBooks = when (pagerState.currentPage) {
                            0 -> savedBooks.filter { it.progress == 0 }
                            1 -> savedBooks.filter { it.progress == 1 }
                            2 -> savedBooks.filter { it.progress == 2 }
                            else -> savedBooks
                        }
                        LazyVerticalGrid(columns = GridCells.Adaptive(screenWidthDp / 3)) {
                            items(filteredBooks) { book ->
                                Column {
                                    if (book.thumbnail != null) {
                                        AsyncImage(
                                            model = book.thumbnail,
                                            contentDescription = null,
                                            modifier = Modifier.Companion
                                                .fillMaxWidth()
                                                .height(120.dp)
                                                .padding(16.dp)
                                                .clickable {
                                                    navController.navigate("${Route.MyBook}/${book.id}")
                                                }
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(R.drawable.unknown),
                                            contentDescription = "thumbnail not found",
                                            modifier = Modifier.Companion
                                                .fillMaxWidth()
                                                .height(120.dp)
                                                .padding(16.dp)
                                                .clickable {
                                                    navController.navigate("${Route.MyBook}/${book.id}")
                                                }
                                        )
                                    }
                                    Text(
                                        text = if (pagerState.currentPage == 0) {
                                            book.registeredDate.substring(0, 10)
                                        } else {
                                            book.updatedDate.substring(0, 10)
                                        },
                                        modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally)
                                    )
                                    if (book.pageCount != null && book.pageCount != 0) {
                                        LinearProgressIndicator(
                                            progress = { book.readPages!!.toFloat() / book.pageCount.toFloat() },
                                            modifier = Modifier.Companion
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            is LibraryUiState.Error -> {
                Column(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Companion.CenterHorizontally,
                ) {
                    Text(
                        text = "エラーが発生しました",
                    )
                }
            }
        }
    }
}