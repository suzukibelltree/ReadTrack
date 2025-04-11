package com.belltree.readtrack.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.belltree.readtrack.R
import com.belltree.readtrack.Route
import com.belltree.readtrack.room.MyBooksViewModel
import com.belltree.readtrack.themecolor.AppColors

/**
 * ライブラリ画面
 * 登録した本の一覧をここで表示する
 * @param navController ナビゲーションコントローラー
 * @param myBooksViewModel 保存された本のViewModel
 */
@Composable
fun LibraryScreen(
    navController: NavController,
    myBooksViewModel: MyBooksViewModel,
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val savedBooks by myBooksViewModel.savedBooks.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val textColor = AppColors.textColor
    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            contentColor = Color.Blue,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Text(
                        text = stringResource(R.string.read_state_unread),
                        color = textColor
                    )
                }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Text(
                        text = stringResource(R.string.read_state_reading),
                        color = textColor
                    )
                }
            )
            Tab(
                selected = selectedTabIndex == 2,
                onClick = { selectedTabIndex = 2 },
                text = {
                    Text(
                        text = stringResource(R.string.read_state_read),
                        color = textColor
                    )
                }
            )
        }
        LazyVerticalGrid(columns = GridCells.Adaptive(screenWidthDp / 3)) {
            // ここでselectedTabIndexに応じて表示する本のリストを変える
            val filteredBooks = when (selectedTabIndex) {
                0 -> savedBooks.filter { it.progress == 0 }
                1 -> savedBooks.filter { it.progress == 1 }
                2 -> savedBooks.filter { it.progress == 2 }
                else -> savedBooks
            }
            items(filteredBooks) { book ->
                Column {
                    if (book.thumbnail != null) {
                        AsyncImage(
                            model = book.thumbnail,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .padding(16.dp)
                                .clickable {
                                    myBooksViewModel.selectBook(book.id)
                                    navController.navigate("${Route.MyBook}/${book.id}")
                                }
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.unknown),
                            contentDescription = "thumbnail not found",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .padding(16.dp)
                                .clickable {
                                    myBooksViewModel.selectBook(book.id)
                                    navController.navigate("${Route.MyBook}/${book.id}")
                                }
                        )
                    }
                    Text(
                        text = if (selectedTabIndex == 0) {
                            book.registeredDate.substring(0, 10)
                        } else {
                            book.updatedDate.substring(0, 10)
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    if (book.pageCount != null) {
                        LinearProgressIndicator(
                            progress = { book.readpage!!.toFloat() / book.pageCount.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                        )
                    }
                }
            }
        }
    }
}