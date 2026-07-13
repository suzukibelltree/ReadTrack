package com.belltree.readtrack.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
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
import com.belltree.readtrack.core.convertYearMonthId
import com.belltree.readtrack.data.local.datastore.getValue
import com.belltree.readtrack.domain.model.ReadLogByMonth
import com.belltree.readtrack.themecolor.getPrimaryColor
import com.belltree.readtrack.ui.navigation.Route
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
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    HomeScreenContent(
        uiState = uiState,
        onBookClick = { bookId -> navController.navigate(Route.MyBook(bookId)) },
        onRegisterClick = { navController.navigate(Route.RegisterProcess) },
        onRetry = homeViewModel::retry,
    )
}

@Composable
internal fun HomeScreenContent(
    uiState: HomeUiState,
    onBookClick: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onRetry: () -> Unit,
) {
    Crossfade(
        targetState = uiState,
        label = "HomeContent"
    ) { state ->
        when (state) {
            is HomeUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomeUiState.Success -> {
                val bindingModel = state.bindingModel
                if (bindingModel.isLibraryEmpty) {
                    HomeWelcomeContent(onRegisterClick = onRegisterClick)
                } else {
                    HomeSuccessContent(
                        bindingModel = bindingModel,
                        onBookClick = onBookClick,
                    )
                }
            }

            is HomeUiState.Error -> {
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
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onRetry) {
                        Text(text = stringResource(R.string.common_retry))
                    }
                }
            }
        }
    }
}

/**
 * 本が1冊以上登録されているときのホーム画面本体
 * 各セクションを下からのスライドイン + フェードインで段階的に表示する
 * @param bindingModel ホーム画面のバインディングモデル
 * @param onBookClick 本がクリックされたときの処理
 */
@Composable
private fun HomeSuccessContent(
    bindingModel: HomeBindingModel,
    onBookClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SectionEntrance(order = 0) {
            // 読了冊数を0から現在値までカウントアップして表示する
            var countUpStarted by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                countUpStarted = true
            }
            val animatedNumOfReadBooks by animateIntAsState(
                targetValue = if (countUpStarted) bindingModel.numOfReadBooks else 0,
                animationSpec = tween(durationMillis = 800),
                label = "NumOfReadBooks"
            )
            Text(
                text = stringResource(
                    R.string.home_number_of_FinishedBooks,
                    animatedNumOfReadBooks
                ),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        SectionEntrance(order = 1) {
            Column {
                HomeSectionTitle(titleResId = R.string.home_last_updatedBook)
                if (bindingModel.recentlyReadBook != null) {
                    MiniBookCard(
                        book = bindingModel.recentlyReadBook,
                        onClick = { bookId -> onBookClick(bookId) },
                        message = stringResource(
                            R.string.home_last_updatedDate,
                            bindingModel.recentlyReadBook.updatedDate
                        )
                    )
                } else {
                    InitialMiniBookCard()
                }
            }
        }
        SectionEntrance(order = 2) {
            Column {
                HomeSectionTitle(titleResId = R.string.home_new_addedBook)
                if (bindingModel.newlyAddedBook != null) {
                    MiniBookCard(
                        book = bindingModel.newlyAddedBook,
                        onClick = { bookId -> onBookClick(bookId) },
                        message = stringResource(
                            R.string.home_new_addedDate,
                            bindingModel.newlyAddedBook.registeredDate
                        )
                    )
                } else {
                    InitialMiniBookCard()
                }
            }
        }
        SectionEntrance(order = 3) {
            if (bindingModel.readLogForGraph.isNotEmpty()) {
                ReadLogGraph(readLogs = bindingModel.readLogForGraph)
            } else {
                ReadLogGraphPlaceholder()
            }
        }
    }
}

/**
 * 本が1冊も登録されていないときに表示するウェルカム画面
 * 最初の1冊の登録へ誘導する
 * @param onRegisterClick 登録ボタンがクリックされたときの処理
 */
@Composable
private fun HomeWelcomeContent(
    onRegisterClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.AutoStories,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.home_welcome_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.home_welcome_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRegisterClick) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = stringResource(R.string.home_register_button))
        }
    }
}

/**
 * ホーム画面のセクションを下からのスライドイン + フェードインで表示する
 * @param order セクションの表示順(0始まり)。順に遅延をかけて段差をつける
 */
@Composable
private fun SectionEntrance(
    order: Int,
    content: @Composable () -> Unit,
) {
    val visibleState = remember {
        MutableTransitionState(false).apply { targetState = true }
    }
    val delayMillis = order * 80
    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(tween(durationMillis = 300, delayMillis = delayMillis)) +
                slideInVertically(
                    tween(durationMillis = 300, delayMillis = delayMillis)
                ) { fullHeight -> fullHeight / 4 },
    ) {
        content()
    }
}

@Composable
private fun HomeSectionTitle(titleResId: Int) {
    Text(
        text = stringResource(titleResId),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

/**
 * 本の簡単な情報を表示するカード
 * HomeScreenにて、最後に更新された本、新しく登録された本の情報を表示するのに使用
 * @param book 本の情報
 * @param onClick 本がクリックされたときの処理
 * @param message 表示するメッセージ
 */
@Composable
fun MiniBookCard(
    book: HomeBookBindingModel,
    onClick: (bookId: String) -> Unit,
    message: String
) {
    Card(
        onClick = { onClick(book.id) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (book.thumbnail != null) {
                AsyncImage(
                    model = book.thumbnail,
                    contentDescription = null,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.unknown),
                    contentDescription = "thumbnail not found",
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoStories,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.home_initialBookCard),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * 読書ログがまだないときにグラフの代わりに表示するプレースホルダ
 */
@Composable
private fun ReadLogGraphPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.home_readLog),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Outlined.BarChart,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.home_graph_placeholder),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/**
 * 読書ログをグラフで表示するコンポーザブル関数
 * @param readLogs 読書ログのリスト
 */
@Composable
fun ReadLogGraph(
    readLogs: List<ReadLogByMonth>
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
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        val chartEntryModel = entryModelOf(
            *readLogs.mapIndexed { index, log ->
                index to log.totalReadPages
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