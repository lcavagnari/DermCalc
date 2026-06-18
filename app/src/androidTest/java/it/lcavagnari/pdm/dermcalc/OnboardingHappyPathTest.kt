/*
 * Copyright (C) 2026 Luca Cavagnari
 *
 * This file is part of DermCalc, final project for the Mobile Device Programming course of Univerità Degli Studi Dell'Insubria.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 */
package it.lcavagnari.pdm.dermcalc

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End-to-end Compose UI test that walks all five onboarding pages and verifies
 * the bottom navigation bar is visible after completing onboarding.
 *
 * Prerequisite semantics added to production code:
 * - "btn_next" / "btn_start" on the navigation button
 * - "input_full_name" on the name OutlinedTextField
 * - "btn_open_date_picker" on the date icon button
 * - "btn_confirm_date" on the DatePickerDialog confirm button
 * - "btn_open_height_picker" / "btn_open_weight_picker" on picker icon buttons
 * - "btn_confirm_picker" on the SnapWheelPickerDialog OK button
 * - "bottom_nav_bar" on the NavigationBar after onboarding
 */
@RunWith(AndroidJUnit4::class)
class OnboardingHappyPathTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    // ── Page 0: Welcome — no inputs, Next is immediately enabled ─────────────

    @Test
    fun onboardingHappyPath_completesAndShowsBottomNav() {
        // Page 0: "Welcome to DermCalc" — no fields, Next is enabled.
        composeRule.onNodeWithTag("btn_next").assertIsDisplayed().assertIsEnabled().performClick()

        // Page 1: "Numbers that matter" — no fields, Next is enabled.
        composeRule.onNodeWithTag("btn_next").assertIsDisplayed().assertIsEnabled().performClick()

        // Page 2: "Always with you" — no fields, Next is enabled.
        composeRule.onNodeWithTag("btn_next").assertIsDisplayed().assertIsEnabled().performClick()

        // Page 3: "Let's get to know you!" — needs name + date-of-birth.
        // Enter a valid full name (two words).
        composeRule.onNodeWithTag("input_full_name").performClick()
        composeRule.onNodeWithTag("input_full_name").performTextInput("Mario Rossi")

        // Open the date picker and select a date.
        composeRule.onNodeWithTag("btn_open_date_picker").performClick()

        // The calendar shows the current month; click any day cell that is selectable.
        // Day "15" is always present in every month — pick it.
        composeRule.waitUntil(5_000L) { composeRule.onAllNodesWithText("15").fetchSemanticsNodes().isNotEmpty() }
        composeRule.onAllNodesWithText("15").onFirst().performClick()

        // Confirm the date selection.
        composeRule.onNodeWithTag("btn_confirm_date").performClick()

        // Sex is not required (isRequired = false) so it auto-validates; Next should now be enabled.
        composeRule.onNodeWithTag("btn_next").assertIsEnabled().performClick()

        // Page 4: "We require a little more information" — needs height + weight.
        // The wheel pickers fire LaunchedEffect(selectedIndex) on first composition,
        // seeding the model with their initial values (170 cm, 70 kg), which are both valid.
        // Open the height picker and immediately confirm to trigger the value callbacks.
        composeRule.onNodeWithTag("btn_open_height_picker").performClick()
        composeRule.onNodeWithTag("btn_confirm_picker").performClick()

        // Open the weight picker and immediately confirm.
        composeRule.onNodeWithTag("btn_open_weight_picker").performClick()
        composeRule.onNodeWithTag("btn_confirm_picker").performClick()

        // The last page shows "Start" instead of "Next".
        composeRule.onNodeWithTag("btn_start").assertIsEnabled().performClick()

        // After onboarding, the bottom navigation bar must be visible.
        composeRule.onNodeWithTag("bottom_nav_bar").assertIsDisplayed()
    }
}
