package com.belltree.readtrack.ui.search.title

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.belltree.readtrack.R
import com.belltree.readtrack.domain.model.BookItem
import com.belltree.readtrack.ui.navigation.Route

/**
 * 本を検索する画面
 * @param titleSearchViewModel 本のリストのViewModel
 * @param searchedBookDetailViewModel 検索結果の詳細を表示するViewModel
 * @param navController ナビゲーションコントローラー
 */
@Composable
fun SearchScreen(
    titleSearchViewModel: TitleSearchViewModel = hiltViewModel(),
    navController: NavController
) {
    var query by remember { mutableStateOf("") }
    var hasSearched by remember { mutableStateOf(false) }
    val books = titleSearchViewModel.bookPagingData.collectAsLazyPagingItems()
    val loadState = books.loadState
    LaunchedEffect(Unit) {
        titleSearchViewModel.clearSearchResults()
    }
    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Companion.CenterHorizontally
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text(text = stringResource(R.string.search_title)) },
            singleLine = true,
            modifier = Modifier.Companion.fillMaxWidth()
        )
        Spacer(modifier = Modifier.Companion.height(8.dp))
        Button(onClick = {
            titleSearchViewModel.updateQuery(query)
            hasSearched = true
        }) {
            Text(text = stringResource(R.string.search_button))
        }
        Spacer(modifier = Modifier.Companion.height(16.dp))

        when {
            loadState.refresh is LoadState.Loading && hasSearched -> CircularProgressIndicator()
            loadState.refresh is LoadState.Error -> Text(text = stringResource(R.string.search_failed))
            else -> BooksCardList(
                books = books,
                hasSearched = hasSearched,
                onBookClick = { book ->
                    titleSearchViewModel.selectBookItem(book)
                    navController.navigate("${Route.BookDetail}/${book.id}")
                }
            )
        }
    }
}

/**
 * 検索された本のリストを表示する
 * @param books 本のリスト
 * @param hasSearched 一度検索されたかどうか
 * @param onBookClick 本がクリックされたときの処理
 */
@Composable
fun BooksCardList(
    books: LazyPagingItems<BookItem>,
    hasSearched: Boolean,
    onBookClick: (BookItem) -> Unit = {}
) {
    // 検索結果がない場合はメッセージを表示
    if (books.itemCount == 0 && hasSearched) {
        Text(
            text = stringResource(R.string.search_no_result)
        )
    } else {
        LazyColumn {
            items(books.itemCount) { index ->
                books[index]?.let { book ->
                    BookCard(book = book, onClick = { onBookClick(book) })
                }
            }
            // 次のページを読み込み中のインジケーターを表示
            item {
                when (books.loadState.append) {
                    is LoadState.Loading -> {
                        Column(
                            modifier = Modifier.Companion
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.Companion.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is LoadState.Error -> {
                        Text(
                            text = stringResource(R.string.search_failed_next_page),
                            modifier = Modifier.Companion.padding(16.dp)
                        )
                    }

                    else -> {}
                }
            }
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
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onClick()
            }
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (book.volumeInfo.imageLinks?.thumbnail != null) {
                AsyncImage(
                    model = book.volumeInfo.imageLinks.thumbnail,
                    contentDescription = "book thumbnail",
                    modifier = Modifier.Companion
                        .size(60.dp)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.unknown),
                    contentDescription = "thumbnail not found",
                    modifier = Modifier.Companion
                        .size(60.dp)
                )
            }
            Column {
                Spacer(modifier = Modifier.Companion.width(8.dp))
                Column {
                    Text(
                        text = book.volumeInfo.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Companion.Ellipsis
                    )
                    Text(
                        text = "著者：${book.volumeInfo.authors?.joinToString(", ") ?: "Unknown"}",
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Companion.Ellipsis
                    )
                    Text(
                        text = "出版社：${book.volumeInfo.publisher ?: "Unknown"}",
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Companion.Ellipsis
                    )
                }
            }
        }
    }
}