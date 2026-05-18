package it.lcavagnari.pdm.dermcalc

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose UI tests that verify the Next button is disabled when required onboarding
 * inputs are missing or invalid.
 *
 * Each test starts from a fresh [MainActivity] instance and navigates to the page
 * under test before asserting the button state.
 */
@RunWith(AndroidJUnit4::class)
class OnboardingValidationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    /** Navigate through the first three pages (no-input pages) to reach page 3 (name/DOB). */
    private fun navigateToPage3() {
        repeat(3) {
            composeRule.onNodeWithTag("btn_next").assertIsDisplayed().performClick()
        }
    }

    /** Navigate to page 4 (height/weight) with valid page 3 inputs already provided. */
    private fun navigateToPage4WithValidPage3() {
        navigateToPage3()
        composeRule.onNodeWithTag("input_full_name").performClick()
        composeRule.onNodeWithTag("input_full_name").performTextInput("Mario Rossi")
        composeRule.onNodeWithTag("btn_open_date_picker").performClick()
        // Select first "15" cell available in the calendar.
        composeRule.waitUntil(5_000L) { composeRule.onAllNodesWithText("15").fetchSemanticsNodes().isNotEmpty() }
        composeRule.onAllNodesWithText("15")[0].performClick()
        composeRule.onNodeWithTag("btn_confirm_date").performClick()
        composeRule.onNodeWithTag("btn_next").performClick()
    }

    // ── Page 3 validation ─────────────────────────────────────────────────────

    @Test
    fun page3_nextDisabled_whenNameIsBlank() {
        navigateToPage3()
        // No name entered yet — date-of-birth also missing.
        composeRule.onNodeWithTag("btn_next").assertIsNotEnabled()
    }

    @Test
    fun page3_nextDisabled_whenNameIsSingleWord() {
        navigateToPage3()
        composeRule.onNodeWithTag("input_full_name").performClick()
        composeRule.onNodeWithTag("input_full_name").performTextInput("Mario")
        // Single word — invalid name; DOB also missing.
        composeRule.onNodeWithTag("btn_next").assertIsNotEnabled()
    }

    @Test
    fun page3_nextDisabled_whenValidNameButNoDob() {
        navigateToPage3()
        composeRule.onNodeWithTag("input_full_name").performClick()
        composeRule.onNodeWithTag("input_full_name").performTextInput("Mario Rossi")
        // Name valid but DOB not yet selected.
        composeRule.onNodeWithTag("btn_next").assertIsNotEnabled()
    }

    // ── Page 4 validation ─────────────────────────────────────────────────────

    @Test
    fun page4_startDisabled_whenNeitherHeightNorWeightSet() {
        navigateToPage4WithValidPage3()
        // Neither height nor weight has been set — Start should be disabled.
        composeRule.onNodeWithTag("btn_start").assertIsNotEnabled()
    }

    @Test
    fun page4_startDisabled_whenOnlyHeightSet() {
        navigateToPage4WithValidPage3()
        // Set height only via the picker.
        composeRule.onNodeWithTag("btn_open_height_picker").performClick()
        composeRule.onNodeWithTag("btn_confirm_picker").performClick()
        // Weight still missing — Start must remain disabled.
        composeRule.onNodeWithTag("btn_start").assertIsNotEnabled()
    }

    @Test
    fun page4_startDisabled_whenOnlyWeightSet() {
        navigateToPage4WithValidPage3()
        // Set weight only via the picker.
        composeRule.onNodeWithTag("btn_open_weight_picker").performClick()
        composeRule.onNodeWithTag("btn_confirm_picker").performClick()
        // Height still missing — Start must remain disabled.
        composeRule.onNodeWithTag("btn_start").assertIsNotEnabled()
    }
}
