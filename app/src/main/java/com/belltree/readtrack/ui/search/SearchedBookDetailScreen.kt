package com.belltree.readtrack.ui.search

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.belltree.readtrack.R
import com.belltree.readtrack.ui.navigation.Route
import kotlinx.coroutines.launch

/**
 * 本の詳細画面
 * SearchScreenで選択した本の詳細を表示する
 * @param navController ナビゲーションコントローラー
 * @param viewmodel 本の詳細を取得するViewModel
 */
@Composable
fun SearchedBookDetailScreen(
    viewmodel: SearchedBookDetailViewModel = hiltViewModel(),
    bookId: String,
    navController: NavController,
) {
    val bookItemState = viewmodel.bookItem.collectAsState()
    val bookItem = bookItemState.value
    val isRegistered = viewmodel.isRegistered.value
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewmodel.loadBookById(bookId)
    }
    if (bookItem == null) {
        Column(
            modifier = Modifier.Companion.fillMaxSize(),
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            Row {
                if (bookItem.volumeInfo.imageLinks != null) {
                    AsyncImage(
                        model = bookItem.volumeInfo.imageLinks.thumbnail,
                        contentDescription = null,
                        modifier = Modifier.Companion
                            .fillMaxWidth(0.4f)
                            .padding(16.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.unknown),
                        contentDescription = "image not found",
                        modifier = Modifier.Companion
                            .fillMaxWidth(0.4f)
                            .padding(16.dp)
                    )
                }
                Column {
                    Text(
                        text = bookItem.volumeInfo.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Companion.Bold,
                    )
                    Text(
                        text = bookItem.volumeInfo.authors.toString(),
                    )
                    Text(
                        text = bookItem.volumeInfo.publishedDate.toString(),
                    )
                }
            }
            Card(
                modifier = Modifier.Companion.fillMaxWidth()
            ) {
                val descriptionHtml = bookItem.volumeInfo.description
                val description = HtmlCompat.fromHtml(
                    descriptionHtml ?: "",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                ).toString()
                if (description == "") {
                    Text(text = stringResource(R.string.bookDetail_null_description))
                } else {
                    if (description.length <= 100) {
                        Text(text = description)
                    } else {
                        var isExpanded by remember { mutableStateOf(false) }
                        Column(
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .animateContentSize()
                        ) {
                            Text(
                                text = if (isExpanded) description
                                else description
                                    .substring(0, 100) + "...",
                            )
                            Row(
                                modifier = Modifier.Companion.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = if (isExpanded) stringResource(R.string.bookDetail_collapse) else stringResource(
                                        R.string.bookDetail_expand
                                    ),
                                    fontWeight = FontWeight.Companion.ExtraBold,
                                    modifier = Modifier.Companion
                                        .clickable { isExpanded = !isExpanded }
                                        .padding(end = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
            //このRowでは本に関する追加情報を表示する
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.Companion.weight(1f))
                Column(
                    horizontalAlignment = Alignment.Companion.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.home_pageCount))
                    Icon(
                        imageVector = Icons.Filled.Book,
                        contentDescription = null,
                        modifier = Modifier.Companion.padding(8.dp)
                    )
                    if (bookItem.volumeInfo.pageCount == null) {
                        Text(text = "Unknown")
                    } else {
                        Text(text = bookItem.volumeInfo.pageCount.toString() + stringResource(R.string.bookDetail_page))
                    }
                }
                Spacer(modifier = Modifier.Companion.weight(1f))
                Column(
                    horizontalAlignment = Alignment.Companion.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.bookDetail_publisher))
                    Icon(
                        imageVector = Icons.Filled.Business,
                        contentDescription = null,
                        modifier = Modifier.Companion.padding(8.dp)
                    )
                    if (bookItem.volumeInfo.publisher == null) {
                        Text(text = "Unknown")
                    } else if (bookItem.volumeInfo.publisher.toString().length > 5) {
                        Text(
                            text = bookItem.volumeInfo.publisher.toString().substring(0, 5) + "..."
                        )
                    } else {
                        Text(text = bookItem.volumeInfo.publisher.toString())
                    }
                }
                Spacer(modifier = Modifier.Companion.weight(1f))
            }
            if (isRegistered) {
                Text(
                    text = stringResource(R.string.bookDetail_alreadyAdded),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Companion.Bold,
                    modifier = Modifier.Companion.padding(16.dp)
                )
                Button(
                    onClick = {
                        navController.navigate("${Route.MyBook}/${bookItem.id}") {
                            popUpTo(Route.Library) {
                                inclusive = true
                            }
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.bookDetail_gotoLibrary))
                }
            } else {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewmodel.addBook(bookItem)
                            Toast.makeText(context, R.string.bookDetail_added, Toast.LENGTH_SHORT)
                                .show()
                            navController.navigate(Route.Library)
                        }
                    },
                    modifier = Modifier.Companion.padding(16.dp)
                ) {
                    Text(text = stringResource(R.string.bookDetail_addButton))
                }
            }
        }
    }

}