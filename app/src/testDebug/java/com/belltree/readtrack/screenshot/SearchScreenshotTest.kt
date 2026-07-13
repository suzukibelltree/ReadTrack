package com.belltree.readtrack.screenshot

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.belltree.readtrack.domain.model.BookItem
import com.belltree.readtrack.domain.model.VolumeInfo
import com.belltree.readtrack.ui.search.title.SearchScreenContent
import com.belltree.readtrack.ui.theme.ReadTrackTheme
import com.github.takahirom.roborazzi.captureRoboImage
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w411dp-h891dp-xxhdpi", application = Application::class)
class SearchScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val notLoadingStates = LoadStates(
        refresh = LoadState.NotLoading(endOfPaginationReached = true),
        prepend = LoadState.NotLoading(endOfPaginationReached = true),
        append = LoadState.NotLoading(endOfPaginationReached = true),
    )

    private fun buildBookItem(id: String, title: String) = BookItem(
        id = id,
        selfLink = "",
        volumeInfo = VolumeInfo(
            title = title,
            authors = listOf("著者A", "著者B"),
            publisher = "テスト出版",
            publishedDate = "2024-01-01",
            description = null,
            imageLinks = null,
            pageCount = 300,
            categories = null,
        )
    )

    private fun setContent(
        query: String,
        hasSearched: Boolean,
        books: List<BookItem>,
    ) {
        composeTestRule.setContent {
            ReadTrackTheme(dynamicColor = false) {
                val pagingItems = flowOf(
                    PagingData.from(books, sourceLoadStates = notLoadingStates)
                ).collectAsLazyPagingItems()
                SearchScreenContent(
                    query = query,
                    hasSearched = hasSearched,
                    books = pagingItems,
                    onQueryChange = {},
                    onSearch = {},
                    onBookClick = {},
                    onRegisterManuallyClick = {},
                )
            }
        }
    }

    @Test
    fun searchScreen_hint() {
        setContent(query = "", hasSearched = false, books = emptyList())
        composeTestRule.onRoot().captureRoboImage(
            "src/test/snapshots/images/SearchScreen_hint.png"
        )
    }

    @Test
    fun searchScreen_noResult() {
        setContent(query = "存在しない本", hasSearched = true, books = emptyList())
        composeTestRule.onRoot().captureRoboImage(
            "src/test/snapshots/images/SearchScreen_noResult.png"
        )
    }

    @Test
    fun searchScreen_results() {
        setContent(
            query = "Kotlin",
            hasSearched = true,
            books = listOf(
                buildBookItem(id = "1", title = "Kotlin実践入門"),
                buildBookItem(id = "2", title = "Kotlinで学ぶAndroidアプリ開発"),
                buildBookItem(id = "3", title = "はじめてのKotlinプログラミング"),
            )
        )
        composeTestRule.onRoot().captureRoboImage(
            "src/test/snapshots/images/SearchScreen_results.png"
        )
    }
}
