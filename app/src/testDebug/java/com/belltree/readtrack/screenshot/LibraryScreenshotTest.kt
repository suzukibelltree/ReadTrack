package com.belltree.readtrack.screenshot

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.belltree.readtrack.ui.library.LibraryBindingModel
import com.belltree.readtrack.ui.library.LibraryBookBindingModel
import com.belltree.readtrack.ui.library.LibraryScreenContent
import com.belltree.readtrack.ui.library.LibraryUiState
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
class LibraryScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun libraryScreen_loading() {
        composeTestRule.setContent {
            ReadTrackTheme(dynamicColor = false) {
                LibraryScreenContent(
                    uiState = LibraryUiState.Loading,
                    onBookClick = {}
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage(
            "src/test/snapshots/images/LibraryScreen_loading.png"
        )
    }

    @Test
    fun libraryScreen_success_withBooks() {
        // 初期表示タブは「未読 (progress == 0)」のため、progress 0 の本を複数用意する
        val books = listOf(
            LibraryBookBindingModel(
                id = "1",
                progress = 0,
                thumbnail = null,
                pageCount = 300,
                readPages = 0,
                registeredDate = "2024/01/10/09:00",
                updatedDate = "2024/01/10/09:00"
            ),
            LibraryBookBindingModel(
                id = "2",
                progress = 0,
                thumbnail = null,
                pageCount = null,
                readPages = null,
                registeredDate = "2024/02/20/12:30",
                updatedDate = "2024/02/20/12:30"
            ),
            LibraryBookBindingModel(
                id = "3",
                progress = 0,
                thumbnail = null,
                pageCount = 250,
                readPages = 0,
                registeredDate = "2024/03/05/18:45",
                updatedDate = "2024/03/05/18:45"
            ),
            LibraryBookBindingModel(
                id = "4",
                progress = 1,
                thumbnail = null,
                pageCount = 200,
                readPages = 100,
                registeredDate = "2024/03/10/08:00",
                updatedDate = "2024/03/12/21:00"
            ),
        )
        composeTestRule.setContent {
            ReadTrackTheme(dynamicColor = false) {
                LibraryScreenContent(
                    uiState = LibraryUiState.Success(LibraryBindingModel(books)),
                    onBookClick = {}
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage(
            "src/test/snapshots/images/LibraryScreen_success_withBooks.png"
        )
    }

    @Test
    fun libraryScreen_success_empty() {
        composeTestRule.setContent {
            ReadTrackTheme(dynamicColor = false) {
                LibraryScreenContent(
                    uiState = LibraryUiState.Success(LibraryBindingModel(emptyList())),
                    onBookClick = {}
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage(
            "src/test/snapshots/images/LibraryScreen_success_empty.png"
        )
    }

    @Test
    fun libraryScreen_error() {
        composeTestRule.setContent {
            ReadTrackTheme(dynamicColor = false) {
                LibraryScreenContent(
                    uiState = LibraryUiState.Error("エラーが発生しました"),
                    onBookClick = {}
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage(
            "src/test/snapshots/images/LibraryScreen_error.png"
        )
    }
}