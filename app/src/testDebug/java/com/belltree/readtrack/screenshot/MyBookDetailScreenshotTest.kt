package com.belltree.readtrack.screenshot

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.belltree.readtrack.domain.model.ReadLog
import com.belltree.readtrack.R
import com.belltree.readtrack.ui.mybookdetail.MyBookDetailBindingModel
import com.belltree.readtrack.ui.mybookdetail.MyBookDetailBookBindingModel
import com.belltree.readtrack.ui.mybookdetail.MyBookDetailUiState
import com.belltree.readtrack.ui.mybookdetail.MyBookScreenContent
import com.belltree.readtrack.ui.theme.ReadTrackTheme
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
class MyBookDetailScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent(uiState: MyBookDetailUiState) {
        composeTestRule.setContent {
            ReadTrackTheme(dynamicColor = false) {
                MyBookScreenContent(
                    bookId = "1",
                    uiState = uiState,
                    showCompleteDialog = false,
                    onUpdateBook = { _, _, _, _ -> },
                    onInsertLog = {},
                    onDeleteBook = {},
                    onOpenCompleteDialog = {},
                    onCloseCompleteDialog = {},
                    onNavigateToLibrary = {},
                )
            }
        }
    }

    @Test
    fun myBookDetailScreen_loading() {
        setContent(MyBookDetailUiState.Loading)
        composeTestRule.onRoot().captureRoboImage(
            "src/test/snapshots/images/MyBookDetailScreen_loading.png"
        )
    }

    @Test
    fun myBookDetailScreen_success_reading() {
        val bindingModel = MyBookDetailBindingModel(
            myBookDetailBookBindingModel = MyBookDetailBookBindingModel(
                id = "1",
                title = "読書中の書籍のタイトル",
                authors = "著者名",
                thumbnail = null,
                progress = 1,
                pageCount = 300,
                readPages = 120,
                comment = "とても面白い本です",
                registeredDate = "2024/01/10",
                updatedDate = "2024/03/01"
            ),
            readLog = listOf(
                ReadLog(
                    logId = 1,
                    bookId = "1",
                    readPages = 50,
                    recordedAt = "2024/01/15/10:00",
                    yearMonthId = 202401
                ),
                ReadLog(
                    logId = 2,
                    bookId = "1",
                    readPages = 70,
                    recordedAt = "2024/03/01/21:30",
                    yearMonthId = 202403
                ),
            )
        )
        setContent(MyBookDetailUiState.Success(bindingModel))
        composeTestRule.onRoot().captureRoboImage(
            "src/test/snapshots/images/MyBookDetailScreen_success_reading.png"
        )
    }

    @Test
    fun myBookDetailScreen_success_noPageCount() {
        // pageCount が null の場合、読了ページ数の入力欄と読書記録は表示されない
        val bindingModel = MyBookDetailBindingModel(
            myBookDetailBookBindingModel = MyBookDetailBookBindingModel(
                id = "1",
                title = "ページ数情報のない書籍",
                authors = "著者名",
                thumbnail = null,
                progress = 0,
                pageCount = null,
                readPages = 0,
                comment = "",
                registeredDate = "2024/02/01",
                updatedDate = "2024/02/01"
            ),
            readLog = emptyList()
        )
        setContent(MyBookDetailUiState.Success(bindingModel))
        composeTestRule.onRoot().captureRoboImage(
            "src/test/snapshots/images/MyBookDetailScreen_success_noPageCount.png"
        )
    }

    @Test
    fun myBookDetailScreen_error() {
        setContent(MyBookDetailUiState.Error(R.string.myBook_error_load))
        composeTestRule.onRoot().captureRoboImage(
            "src/test/snapshots/images/MyBookDetailScreen_error.png"
        )
    }
}