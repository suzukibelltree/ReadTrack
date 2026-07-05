package com.belltree.readtrack.screenshot

import android.app.Application
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.belltree.readtrack.domain.model.ReadLogByMonth
import com.belltree.readtrack.ui.home.HomeBindingModel
import com.belltree.readtrack.ui.home.HomeBookBindingModel
import com.belltree.readtrack.ui.home.HomeScreenContent
import com.belltree.readtrack.ui.home.HomeUiState
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w411dp-h891dp-xxhdpi", application = Application::class)
class HomeScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_loading() {
        composeTestRule.setContent {
            MaterialTheme {
                HomeScreenContent(
                    uiState = HomeUiState.Loading,
                    onBookClick = {}
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage(
            "src/test/snapshots/images/HomeScreen_loading.png"
        )
    }

    @Test
    fun homeScreen_success_withBooks() {
        val bindingModel = HomeBindingModel(
            numOfReadBooks = 5,
            recentlyReadBook = HomeBookBindingModel(
                id = "1",
                title = "最近読んだ書籍のタイトル",
                thumbnail = null,
                registeredDate = "2024/01/10",
                updatedDate = "2024/03/01"
            ),
            newlyAddedBook = HomeBookBindingModel(
                id = "2",
                title = "新しく追加した書籍のタイトル",
                thumbnail = null,
                registeredDate = "2024/03/15",
                updatedDate = "2024/03/15"
            ),
            readLogForGraph = listOf(
                ReadLogByMonth(yearMonthId = 202401, totalReadPages = 120),
                ReadLogByMonth(yearMonthId = 202402, totalReadPages = 200),
                ReadLogByMonth(yearMonthId = 202403, totalReadPages = 80),
            )
        )
        composeTestRule.setContent {
            MaterialTheme {
                HomeScreenContent(
                    uiState = HomeUiState.Success(bindingModel),
                    onBookClick = {}
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage(
            "src/test/snapshots/images/HomeScreen_success_withBooks.png"
        )
    }

    @Test
    fun homeScreen_success_empty() {
        val bindingModel = HomeBindingModel(
            numOfReadBooks = 0,
            recentlyReadBook = null,
            newlyAddedBook = null,
            readLogForGraph = emptyList()
        )
        composeTestRule.setContent {
            MaterialTheme {
                HomeScreenContent(
                    uiState = HomeUiState.Success(bindingModel),
                    onBookClick = {}
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage(
            "src/test/snapshots/images/HomeScreen_success_empty.png"
        )
    }

    @Test
    fun homeScreen_error() {
        composeTestRule.setContent {
            MaterialTheme {
                HomeScreenContent(
                    uiState = HomeUiState.Error("エラーが発生しました"),
                    onBookClick = {}
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage(
            "src/test/snapshots/images/HomeScreen_error.png"
        )
    }
}