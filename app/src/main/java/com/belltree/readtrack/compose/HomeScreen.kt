package com.belltree.readtrack.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.belltree.readtrack.R
import com.belltree.readtrack.Route
import com.belltree.readtrack.convertYearMonthId
import com.belltree.readtrack.datastore.getValue
import com.belltree.readtrack.network.BookData
import com.belltree.readtrack.room.HomeViewModel
import com.belltree.readtrack.room.ReadLog
import com.belltree.readtrack.themecolor.getPrimaryColor
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

/**
 * ホーム画面
 * アプリが起動したらこの画面からスタートする
 * 直近で情報を更新した本、直近で登録された本の情報を表示する
 * @param navController ナビゲーションコントローラー
 * @param homeViewModel 読書ログのViewModel
 */
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
) {
    val savedBooks = homeViewModel.allBooks.collectAsState()
    val finishedBooks = savedBooks.value.filter { it.progress == 2 }
    // もっとも最近に更新された本のインスタンスを取得
    val updatedBook by remember(savedBooks.value) {
        derivedStateOf { savedBooks.value.maxByOrNull { it.updatedDate } }
    }
    val newBook by remember(savedBooks.value) {
        derivedStateOf { savedBooks.value.maxByOrNull { it.registeredDate } }
    }
    val readLogs = homeViewModel.allLogs.collectAsState()
    // 直近4か月分の読書ログを取得
    val recentReadLogs = readLogs.value.sortedByDescending { it.yearMonthId }.take(4).reversed()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.home_number_of_FinishedBooks, finishedBooks.size),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        Text(
            text = stringResource(R.string.home_last_updatedBook),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        if (updatedBook != null) {
            MiniBookCard(
                book = updatedBook!!,
                navController = navController,
                message = stringResource(R.string.home_last_updatedDate, updatedBook!!.updatedDate)
            )
        } else {
            InitialMiniBookCard()
        }
        Text(
            text = stringResource(R.string.home_new_addedBook),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        if (newBook != null) {
            MiniBookCard(
                book = newBook!!,
                navController = navController,
                message = stringResource(R.string.home_new_addedDate, newBook!!.registeredDate)
            )
        } else {
            InitialMiniBookCard()
        }
        ReadLogGraph(readLogs = recentReadLogs)
    }
}

/**
 * 本の簡単な情報を表示するカード
 * HomeScreenにて、最後に更新された本、新しく登録された本の情報を表示するのに使用
 * @param book 本の情報
 * @param navController ナビゲーションコントローラー
 * @param message 表示するメッセージ
 */
@Composable
fun MiniBookCard(
    book: BookData,
    navController: NavController,
    message: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = { navController.navigate("${Route.MyBook}/${book.id}") }
    ) {
        Row {
            if (book.thumbnail != null) {
                AsyncImage(
                    model = book.thumbnail,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.unknown),
                    contentDescription = "thumbnail not found",
                    modifier = Modifier.size(100.dp)
                )
            }
            Column {
                Text(
                    text = book.title,
                    fontWeight = FontWeight.Bold
                )
                Text(text = message)
            }
        }
    }
}

/**
 * 本がまだ登録されていないときに表示するBookCard
 */
@Composable
fun InitialMiniBookCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.home_initialBookCard),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

/**
 * 読書ログをグラフで表示するコンポーザブル関数
 * @param readLogs 読書ログのリスト
 */
@Composable
fun ReadLogGraph(
    readLogs: List<ReadLog>
) {
    val context = LocalContext.current
    val themeColor by getValue(context, "theme_color").collectAsState(initial = "")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.home_readLog),
            fontWeight = FontWeight.Bold,
        )
        val chartEntryModel = entryModelOf(
            *readLogs.mapIndexed { index, log ->
                index to log.readPages.toFloat()
            }.toTypedArray()
        )
        Chart(
            chart = ColumnChart(
                listOf(
                    lineComponent(
                        color = getPrimaryColor(isSystemInDarkTheme(), themeColor),
                        thickness = 8.dp
                    )
                ),
            ),
            model = chartEntryModel,
            startAxis = rememberStartAxis(
                title = stringResource(R.string.home_pageCount),
                valueFormatter = { value, _ -> value.toInt().toString() }
            ),
            bottomAxis = rememberBottomAxis(
                title = stringResource(R.string.home_yearMonth),
                valueFormatter = { value, _ ->
                    // valueはインデックスなので、対応するreadLogsのyearMonthIdを取得
                    val index = value.toInt()
                    if (index in readLogs.indices) {
                        val formatted = convertYearMonthId(readLogs[index].yearMonthId)
                        formatted
                    } else {
                        ""
                    }
                }
            ),
        )
    }
}
