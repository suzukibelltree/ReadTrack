package com.belltree.readtrack.ui.library

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.belltree.readtrack.R
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
    val uiState by libraryViewModel.uiState.collectAsStateWithLifecycle()
    LibraryScreenContent(
        uiState = uiState,
        onBookClick = { bookId -> navController.navigate(Route.MyBook(bookId)) },
        onRegisterClick = { navController.navigate(Route.RegisterProcess) },
    )
}

@Composable
internal fun LibraryScreenContent(
    uiState: LibraryUiState,
    onBookClick: (String) -> Unit,
    onRegisterClick: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = 0) { 3 }
    val scope = rememberCoroutineScope()
    val tabTitleResIds = listOf(
        R.string.read_state_unread,
        R.string.read_state_reading,
        R.string.read_state_read,
    )
    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabTitleResIds.forEachIndexed { index, titleResId ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(text = stringResource(titleResId)) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Crossfade(
            targetState = uiState,
            label = "LibraryContent"
        ) { state ->
            when (state) {
                is LibraryUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is LibraryUiState.Success -> {
                    val savedBooks = state.bindingModel.libraryBookBindingModel
                    HorizontalPager(state = pagerState) { page ->
                        val filteredBooks = remember(savedBooks, page) {
                            savedBooks.filter { it.progress == page }
                        }
                        if (filteredBooks.isEmpty()) {
                            LibraryEmptyContent(
                                page = page,
                                onRegisterClick = onRegisterClick,
                            )
                        } else {
                            LibraryBookGrid(
                                books = filteredBooks,
                                showRegisteredDate = page == 0,
                                onBookClick = onBookClick,
                            )
                        }
                    }
                }

                is LibraryUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(state.messageResId),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

/**
 * 登録した本をグリッドで表示する
 * @param books 表示する本のリスト
 * @param showRegisteredDate true なら登録日、false なら更新日を表示する
 * @param onBookClick 本がクリックされたときの処理
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LibraryBookGrid(
    books: List<LibraryBookBindingModel>,
    showRegisteredDate: Boolean,
    onBookClick: (String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 104.dp),
        modifier = Modifier.fillMaxSize(),
        // FAB と最下段のカードが重ならないよう下側に余白を確保する
        contentPadding = PaddingValues(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 88.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(books, key = { it.id }) { book ->
            LibraryBookCard(
                book = book,
                showRegisteredDate = showRegisteredDate,
                onClick = onBookClick,
                modifier = Modifier.animateItemPlacement(),
            )
        }
    }
}

/**
 * 本1冊分のカード
 * サムネイル・タイトル・日付・読書進捗を表示する
 * @param book 本の情報
 * @param showRegisteredDate true なら登録日、false なら更新日を表示する
 * @param onClick カードがクリックされたときの処理
 */
@Composable
private fun LibraryBookCard(
    book: LibraryBookBindingModel,
    showRegisteredDate: Boolean,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = { onClick(book.id) },
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                if (book.thumbnail != null) {
                    AsyncImage(
                        model = book.thumbnail,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.unknown),
                        contentDescription = "thumbnail not found",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (showRegisteredDate) {
                        book.registeredDate.take(10)
                    } else {
                        book.updatedDate.take(10)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (book.pageCount != null && book.pageCount != 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = {
                            ((book.readPages ?: 0).toFloat() / book.pageCount.toFloat())
                                .coerceIn(0f, 1f)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(2.dp)),
                    )
                }
            }
        }
    }
}

/**
 * タブに表示する本が1冊もないときの Empty State
 * 未読タブでは登録画面への導線を表示する
 * @param page 表示中のタブ(0:未読, 1:読書中, 2:読了)
 * @param onRegisterClick 登録ボタンがクリックされたときの処理
 */
@Composable
private fun LibraryEmptyContent(
    page: Int,
    onRegisterClick: () -> Unit,
) {
    val content = when (page) {
        0 -> EmptyTabContent(
            icon = Icons.Outlined.LibraryBooks,
            titleResId = R.string.library_empty_unread_title,
            messageResId = R.string.library_empty_unread_message,
            showRegisterButton = true,
        )

        1 -> EmptyTabContent(
            icon = Icons.Outlined.AutoStories,
            titleResId = R.string.library_empty_reading_title,
            messageResId = R.string.library_empty_reading_message,
            showRegisterButton = false,
        )

        else -> EmptyTabContent(
            icon = Icons.Outlined.CheckCircle,
            titleResId = R.string.library_empty_read_title,
            messageResId = R.string.library_empty_read_message,
            showRegisterButton = false,
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = content.icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(content.titleResId),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(content.messageResId),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (content.showRegisterButton) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRegisterClick) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = stringResource(R.string.library_empty_register_button))
            }
        }
    }
}

private data class EmptyTabContent(
    val icon: ImageVector,
    @StringRes val titleResId: Int,
    @StringRes val messageResId: Int,
    val showRegisterButton: Boolean,
)