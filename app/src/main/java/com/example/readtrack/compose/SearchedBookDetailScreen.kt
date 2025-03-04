package com.example.readtrack.compose

import android.widget.Toast
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.readtrack.ConvertBookItemToBookData
import com.example.readtrack.R
import com.example.readtrack.ReadTrackApplication
import com.example.readtrack.Route
import com.example.readtrack.network.BookItem
import kotlinx.coroutines.launch

/**
 * 本の詳細画面
 * SearchScreenで選択した本の詳細を表示する
 * @param navController ナビゲーションコントローラー
 * @param bookItem 選択された本の情報
 */
@Composable
fun BookDetail(
    navController: NavController,
    bookItem: BookItem,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val app = context.applicationContext as ReadTrackApplication
    val db = app.appContainer.booksRepository
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            AsyncImage(
                model = bookItem.volumeInfo.imageLinks.thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(16.dp)
            )
            Column {
                Text(
                    text = bookItem.volumeInfo.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
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
            modifier = Modifier.fillMaxWidth()
        ) {
            if (bookItem.volumeInfo.description.toString().length <= 100) {
                Text(text = bookItem.volumeInfo.description.toString())
            } else {
                var isExpanded by remember { mutableStateOf(false) }
                Column {
                    Text(
                        text = if (isExpanded) bookItem.volumeInfo.description.toString()
                        else bookItem.volumeInfo.description.toString().substring(0, 100) + "...",
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = if (isExpanded) stringResource(R.string.bookDetail_collapse) else stringResource(
                                R.string.bookDetail_expand
                            ),
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier
                                .clickable { isExpanded = !isExpanded }
                                .padding(end = 16.dp)
                        )
                    }
                }
            }
        }
        //このRowでは本に関する追加情報を表示する
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.home_pageCount))
                Icon(
                    imageVector = Icons.Filled.Book,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp)
                )
                Text(text = bookItem.volumeInfo.pageCount.toString() + stringResource(R.string.bookDetail_page))
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.bookDetail_publisher))
                Icon(
                    imageVector = Icons.Filled.Business,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp)
                )
                if (bookItem.volumeInfo.publisher.toString().length > 5) {
                    Text(text = bookItem.volumeInfo.publisher.toString().substring(0, 5) + "...")
                } else {
                    Text(text = bookItem.volumeInfo.publisher.toString())
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        Button(
            onClick = {
                val book = ConvertBookItemToBookData(bookItem)
                coroutineScope.launch {
                    db.insert(book)
                }
                Toast.makeText(context, R.string.bookDetail_added, Toast.LENGTH_SHORT).show()
                navController.navigate(Route.Library)
            }, modifier = Modifier.padding(16.dp)
        ) {
            Text(text = stringResource(R.string.bookDetail_addButton))
        }
    }
}
