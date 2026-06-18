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

import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.lcavagnari.pdm.dermcalc.ui.component.ToolSaveButton
import it.lcavagnari.pdm.dermcalc.ui.theme.DermCalcTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for [ToolSaveButton] that verify:
 * - The 3-tap confirmation behavior (tap 1 → "Save?", tap 2 → stays "Save?", tap 3 → fires callback)
 * - Button disabled state is respected
 */
@RunWith(AndroidJUnit4::class)
class ToolSaveButtonTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun buttonDisplaysConfirmTextInitially() {
        composeRule.setContent {
            DermCalcTheme {
                Surface {
                    ToolSaveButton(
                        enabled = true,
                        onSaveResult = {}
                    )
                }
            }
        }

        composeRule.onNodeWithTag("tool_btn_save")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Confirm")
            .assertIsDisplayed()
    }

    @Test
    fun firstTapChangesTextToSaveQuestion() {
        composeRule.setContent {
            DermCalcTheme {
                Surface {
                    ToolSaveButton(
                        enabled = true,
                        onSaveResult = {}
                    )
                }
            }
        }

        // First tap
        composeRule.onNodeWithTag("tool_btn_save").performClick()

        composeRule.onNodeWithText("Save?")
            .assertIsDisplayed()
    }

    @Test
    fun secondTapKeepsTextAsSaveQuestion() {
        composeRule.setContent {
            DermCalcTheme {
                Surface {
                    ToolSaveButton(
                        enabled = true,
                        onSaveResult = {}
                    )
                }
            }
        }

        // First tap
        composeRule.onNodeWithTag("tool_btn_save").performClick()
        composeRule.onNodeWithText("Save?").assertIsDisplayed()

        // Second tap
        composeRule.onNodeWithTag("tool_btn_save").performClick()

        // Should now show "Save" (without question mark) not "Save?"
        composeRule.onNodeWithText("Save")
            .assertIsDisplayed()
    }

    @Test
    fun thirdTapFiresCallbackAndResetsState() {
        var callbackFired = false

        composeRule.setContent {
            DermCalcTheme {
                Surface {
                    ToolSaveButton(
                        enabled = true,
                        onSaveResult = { callbackFired = true }
                    )
                }
            }
        }

        // First tap
        composeRule.onNodeWithTag("tool_btn_save").performClick()

        // Second tap
        composeRule.onNodeWithTag("tool_btn_save").performClick()

        // Verify callback not yet fired
        assert(!callbackFired)

        // Third tap
        composeRule.onNodeWithTag("tool_btn_save").performClick()

        // Callback should have fired
        assert(callbackFired)

        // State should be reset, so text should return to "Confirm"
        composeRule.onNodeWithText("Confirm")
            .assertIsDisplayed()
    }

    @Test
    fun disabledButtonCannotBeClicked() {
        var callbackFired = false

        composeRule.setContent {
            DermCalcTheme {
                Surface {
                    ToolSaveButton(
                        enabled = false,
                        onSaveResult = { callbackFired = true }
                    )
                }
            }
        }

        composeRule.onNodeWithTag("tool_btn_save")
            .assertIsNotEnabled()

        // Try clicking the disabled button
        composeRule.onNodeWithTag("tool_btn_save").performClick()

        // Callback should not fire
        assert(!callbackFired)

        // Text should remain "Confirm"
        composeRule.onNodeWithText("Confirm")
            .assertIsDisplayed()
    }

    @Test
    fun buttonBecomesEnabledWhenPropertyChanges() {
        val isEnabled = mutableStateOf(false)

        composeRule.setContent {
            DermCalcTheme {
                Surface {
                    ToolSaveButton(
                        enabled = isEnabled.value,
                        onSaveResult = {}
                    )
                }
            }
        }

        // Initially disabled
        composeRule.onNodeWithTag("tool_btn_save")
            .assertIsNotEnabled()

        // Enable the button
        composeRule.runOnUiThread {
            isEnabled.value = true
        }

        // Should now be enabled
        composeRule.onNodeWithTag("tool_btn_save")
            .assertIsEnabled()
    }
}
