package com.belltree.readtrack

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.material3.Text
import com.belltree.readtrack.domain.model.ReadLogByMonth
import com.belltree.readtrack.ui.components.expandToggle
import com.belltree.readtrack.ui.home.ReadLogGraph
import com.belltree.readtrack.ui.mybookdetail.ReadProgress
import com.belltree.readtrack.ui.mybookdetail.ReadStateCard
import com.belltree.readtrack.ui.theme.ReadTrackTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Issue #85 のアクセシビリティ対応が TalkBack に公開するセマンティクスを検証する
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], qualifiers = "ja", application = Application::class)
class AccessibilitySemanticsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun hasRole(role: Role) =
        SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

    private fun hasStateDescription(value: String) =
        SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, value)

    @Test
    fun `ReadStateCard exposes radio button role and moves selection on click`() {
        composeTestRule.setContent {
            ReadTrackTheme(dynamicColor = false) {
                var selected by remember { mutableStateOf(ReadProgress.UNREAD) }
                Column {
                    listOf(ReadProgress.UNREAD, ReadProgress.READING, ReadProgress.READ).forEach { progress ->
                        ReadStateCard(
                            progress = progress,
                            selected = selected == progress,
                            icon = R.drawable.frame,
                            onProgressChange = { selected = progress },
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("未読")
            .assert(hasRole(Role.RadioButton))
            .assertIsSelected()
        composeTestRule.onNodeWithText("読書中").assertIsNotSelected()

        composeTestRule.onNodeWithText("読書中").performClick()

        composeTestRule.onNodeWithText("未読").assertIsNotSelected()
        composeTestRule.onNodeWithText("読書中").assertIsSelected()
    }

    @Test
    fun `expandToggle exposes button role and toggles state description`() {
        composeTestRule.setContent {
            ReadTrackTheme(dynamicColor = false) {
                var expanded by remember { mutableStateOf(false) }
                Text(
                    text = "見出し",
                    modifier = Modifier.expandToggle(
                        expanded = expanded,
                        onToggle = { expanded = !expanded },
                    ),
                )
            }
        }

        composeTestRule.onNodeWithText("見出し")
            .assert(hasRole(Role.Button))
            .assert(hasStateDescription("折りたたみ中"))
            .performClick()

        composeTestRule.onNodeWithText("見出し")
            .assert(hasStateDescription("展開中"))
    }

    @Test
    fun `ReadLogGraph has text summary of monthly pages`() {
        composeTestRule.setContent {
            ReadTrackTheme(dynamicColor = false) {
                ReadLogGraph(
                    readLogs = listOf(
                        ReadLogByMonth(yearMonthId = 202609, totalReadPages = 120),
                        ReadLogByMonth(yearMonthId = 202610, totalReadPages = 80),
                    )
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("月別の読書ページ数のグラフ。2026/09 120ページ、2026/10 80ページ")
            .assertExists()
    }
}
