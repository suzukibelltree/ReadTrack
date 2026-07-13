package com.belltree.readtrack.ui.search.title

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.belltree.readtrack.R
import com.belltree.readtrack.domain.model.BookItem
import com.belltree.readtrack.ui.navigation.Route

/**
 * 本を検索する画面
 * @param titleSearchViewModel 本のリストのViewModel
 * @param navController ナビゲーションコントローラー
 */
@Composable
fun SearchScreen(
    titleSearchViewModel: TitleSearchViewModel = hiltViewModel(),
    navController: NavController
) {
    var query by rememberSaveable { mutableStateOf("") }
    var hasSearched by rememberSaveable { mutableStateOf(false) }
    val books = titleSearchViewModel.bookPagingData.collectAsLazyPagingItems()

    // 画面を開いた時に前回の検索結果をクリア
    LaunchedEffect(Unit) {
        titleSearchViewModel.clearSearchResults()
    }

    SearchScreenContent(
        query = query,
        hasSearched = hasSearched,
        books = books,
        onQueryChange = { query = it },
        onSearch = {
            titleSearchViewModel.updateQuery(query)
            hasSearched = true
        },
        onBookClick = { book ->
            titleSearchViewModel.selectBookItem(book)
            navController.navigate(Route.BookDetail(book.id))
        },
        onRegisterManuallyClick = { navController.navigate(Route.RegisterManually) },
    )
}

/**
 * 検索画面の表示フェーズ
 */
private enum class SearchPhase { Hint, Loading, Error, NoResult, Results }

@Composable
internal fun SearchScreenContent(
    query: String,
    hasSearched: Boolean,
    books: LazyPagingItems<BookItem>,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBookClick: (BookItem) -> Unit,
    onRegisterManuallyClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val search = {
        if (query.isNotBlank()) {
            focusManager.clearFocus()
            onSearch()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(text = stringResource(R.string.search_title)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.search_clear),
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { search() }),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        val phase = when {
            !hasSearched -> SearchPhase.Hint
            books.loadState.refresh is LoadState.Loading -> SearchPhase.Loading
            books.loadState.refresh is LoadState.Error -> SearchPhase.Error
            books.itemCount == 0 -> SearchPhase.NoResult
            else -> SearchPhase.Results
        }
        Crossfade(
            targetState = phase,
            label = "SearchContent"
        ) { currentPhase ->
            when (currentPhase) {
                SearchPhase.Hint -> {
                    SearchMessageContent(
                        icon = Icons.Outlined.Search,
                        title = stringResource(R.string.search_hint_title),
                        message = stringResource(R.string.search_hint_message),
                    )
                }

                SearchPhase.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                SearchPhase.Error -> {
                    SearchMessageContent(
                        icon = Icons.Outlined.ErrorOutline,
                        title = stringResource(R.string.search_failed),
                        message = null,
                    ) {
                        Button(onClick = { books.retry() }) {
                            Text(text = stringResource(R.string.common_retry))
                        }
                    }
                }

                SearchPhase.NoResult -> {
                    SearchMessageContent(
                        icon = Icons.Outlined.SearchOff,
                        title = stringResource(R.string.search_no_result_title),
                        message = stringResource(R.string.search_no_result),
                    ) {
                        OutlinedButton(onClick = onRegisterManuallyClick) {
                            Text(text = stringResource(R.string.search_register_manually_button))
                        }
                    }
                }

                SearchPhase.Results -> {
                    SearchResultList(
                        books = books,
                        onBookClick = onBookClick,
                    )
                }
            }
        }
    }
}

/**
 * 検索結果のリスト
 * @param books ページングされた検索結果
 * @param onBookClick 本がクリックされたときの処理
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SearchResultList(
    books: LazyPagingItems<BookItem>,
    onBookClick: (BookItem) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            count = books.itemCount,
            key = books.itemKey { it.id },
        ) { index ->
            books[index]?.let { book ->
                BookCard(
                    book = book,
                    onClick = { onBookClick(book) },
                    modifier = Modifier.animateItemPlacement(),
                )
            }
        }

        // ページング中インジケータ
        item {
            when (books.loadState.append) {
                is LoadState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is LoadState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.search_failed_next_page),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = { books.retry() }) {
                            Text(text = stringResource(R.string.common_retry))
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

/**
 * 検索前のヒント・結果なし・エラーなどのメッセージ表示
 * @param icon 表示するアイコン
 * @param title タイトル
 * @param message 補足メッセージ(不要なら null)
 * @param action 下部に表示するアクション(ボタンなど)
 */
@Composable
private fun SearchMessageContent(
    icon: ImageVector,
    title: String,
    message: String?,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        if (message != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        if (action != null) {
            Spacer(modifier = Modifier.height(24.dp))
            action()
        }
    }
}

/**
 * 検索された本一冊の情報を表示するカード
 * @param book 本の情報
 * @param onClick カードがクリックされたときの処理
 */
@Composable
fun BookCard(
    book: BookItem,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (book.volumeInfo.imageLinks?.thumbnail != null) {
                AsyncImage(
                    model = book.volumeInfo.imageLinks.thumbnail,
                    contentDescription = "book thumbnail",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.unknown),
                    contentDescription = "thumbnail not found",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = book.volumeInfo.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.search_author,
                        book.volumeInfo.authors?.joinToString(", ")
                            ?: stringResource(R.string.search_unknown)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(
                        R.string.search_publisher,
                        book.volumeInfo.publisher
                            ?: stringResource(R.string.search_unknown)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}