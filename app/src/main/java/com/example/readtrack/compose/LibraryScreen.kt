package com.example.readtrack.compose

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.readtrack.Route
import com.example.readtrack.room.SavedBooksViewModel

/**
 * ライブラリ画面
 * 登録した本の一覧をここで表示する
 * @param navController ナビゲーションコントローラー
 * @param savedBooksViewModel 保存された本のViewModel
 */
@Composable
fun LibraryScreen(
    navController: NavController,
    savedBooksViewModel: SavedBooksViewModel,
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val savedBooks by savedBooksViewModel.savedBooks.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            contentColor = Color.Blue,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("未読") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("読書中") }
            )
            Tab(
                selected = selectedTabIndex == 2,
                onClick = { selectedTabIndex = 2 },
                text = { Text("読了") }
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
                    AsyncImage(
                        model = book.thumbnail,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(16.dp)
                            .clickable {
                                savedBooksViewModel.selectBook(book.id)
                                navController.navigate("${Route.MyBook}/${book.id}")
                            }
                    )
                    LinearProgressIndicator(
                        progress = { book.readpage!!.toFloat() / book.pageCount!!.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    )
                }
            }
        }
    }
}