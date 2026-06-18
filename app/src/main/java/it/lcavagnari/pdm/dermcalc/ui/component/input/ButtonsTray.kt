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
package it.lcavagnari.pdm.dermcalc.ui.component.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalDarkTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalToggleDarkTheme


/** Position of the label relative to the icon for labeled buttons. */
enum class LabelPosition {
    /** Label appears above the icon. */
    Above,

    /** Label appears below the icon. */
    Below,

    /** Label appears to the left of the icon. */
    Start,

    /** Label appears to the right of the icon. */
    End,
}


/**
 * A close (X) icon button.
 *
 * No label support — designed for compact overlays and dialogs.
 *
 * @param onClick callback invoked when the button is tapped.
 * @param modifier modifier applied to the [IconButton].
 * @param tint tint for the close icon.
 */
@Composable
fun CloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.btn_close_description),
            tint = tint,
        )
    }
}


/**
 * A reset icon button with an optional text label and a built-in confirmation dialog.
 *
 * Uses the 3-tap confirm pattern via [ActionConfirmDialog] to prevent accidental resets.
 * The label is displayed at the position specified by [labelPosition].
 *
 * @param onReset callback invoked when the user confirms the dialog.
 * @param modifier modifier applied to the [IconButton].
 * @param toolLabel name of the tool being reset, shown in the default dialog body.
 * @param soulColor tint for the reset icon.
 * @param label text label shown near the icon; empty string hides the label.
 * @param labelPosition where to place the label relative to the icon.
 * @param dialogTitle title of the confirmation dialog.
 * @param dialogBody body text of the confirmation dialog.
 * @param dialogConfirmLabel text for the confirm button in the dialog.
 */
@Composable
fun ResetButton(
    modifier: Modifier = Modifier,
    label: String = "",
    toolLabel: String = "",
    soulColor: Color = MaterialTheme.colorScheme.primary,
    labelPosition: LabelPosition = LabelPosition.Below,
    dialogTitle: String = stringResource(id = R.string.reset_dialog_title),
    dialogBody: String = stringResource(id = R.string.reset_dialog_body, toolLabel),
    dialogConfirmLabel: String = stringResource(id = R.string.btn_reset),
    onReset: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ActionConfirmDialog(
            title = dialogTitle,
            body = dialogBody,
            confirmLabel = dialogConfirmLabel,
            onConfirm = onReset,
            onDismiss = { showDialog = false }
        )
    }

    val icon: @Composable () -> Unit = {
        Icon(
            painter = painterResource(id = R.drawable.ic_reset_button),
            contentDescription = stringResource(id = R.string.btn_reset),
            tint = soulColor,
            modifier = Modifier.size(18.dp),
        )
    }

    if (label.isNotEmpty()) {
        Box(
            modifier = modifier
                .height(64.dp)
                .widthIn(min = 80.dp)
                .clickable(onClick = { showDialog = true }),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_reset_button),
                modifier = Modifier
                    .size(20.dp)
                    .padding(bottom = 3.dp)
                    .align(Alignment.Center),
                contentDescription = stringResource(id = R.string.btn_reset),
                tint = soulColor,
            )

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .align(Alignment.BottomCenter),
            )
        }

    } else IconButton(
        modifier = modifier,
        onClick = { showDialog = true },
        content = { icon() }
    )
}


/**
 * A language-picker icon button (W.I.P).
 *
 * @param onClick callback invoked when the button is tapped.
 * @param modifier modifier applied to the clickable container.
 * @param tint tint for the language icon.
 */
@Composable
fun LangButton(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, modifier = modifier.size(35.dp)) {
        Icon(
            painter = painterResource(id = R.drawable.ic_lang_button),
            contentDescription = "Language",
            tint = tint,
            modifier = Modifier.size(20.dp),
        )
    }
}


/**
 * A theme-toggle icon button that displays the current mode (light/dark).
 *
 * @param modifier modifier applied to the clickable container.
 * @param tint tint for the theme icon.
 */
@Composable
fun ThemeToggleButton(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
) {
    val toggleDarkTheme = LocalToggleDarkTheme.current

    IconButton(onClick = { toggleDarkTheme() }, modifier = modifier.size(40.dp)) {
        Icon(
            imageVector = if (LocalDarkTheme.current) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
            contentDescription = "Dark/Light mode",
            tint = tint,
            modifier = Modifier.size(30.dp),
        )
    }
}


/**
 * A tray hub that renders a horizontal row of tool buttons.
 *
 * Each button is shown **only** when its corresponding callback is non-null.
 * Pass `null` (or omit the parameter) to hide a button entirely.
 *
 * @param modifier modifier applied to the outer [Row].
 * @param iconTint default tint used by buttons that do not have their own colour.
 * @param onToggleTheme callback to toggle dark/light theme; `null` hides the button.
 * @param onLangClick callback to open the language picker; `null` hides the button.
 * @param onClose callback to close whatever overlay contains the tray; `null` hides the button.
 * @param onReset callback to reset/clear; `null` hides the button.
 * @param resetToolLabel tool name shown in the reset confirmation dialog body.
 * @param resetSoulColor tint for the reset icon.
 * @param resetLabel text label for the reset button; empty hides the label.
 * @param resetLabelPosition where to place the reset label relative to its icon.
 * @param resetDialogTitle title of the reset confirmation dialog.
 * @param resetDialogBody body text of the reset confirmation dialog.
 * @param resetDialogConfirmLabel text for the confirm button in the reset dialog.
 */
@Composable
fun ButtonsTray(
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    resetToolLabel: String = "",
    resetSoulColor: Color = MaterialTheme.colorScheme.primary,
    resetLabel: String = "",
    resetLabelPosition: LabelPosition = LabelPosition.Below,
    resetDialogTitle: String = stringResource(id = R.string.reset_dialog_title),
    resetDialogBody: String = stringResource(id = R.string.reset_dialog_body, ""),
    resetDialogConfirmLabel: String = stringResource(id = R.string.btn_reset),
    onToggleTheme: (() -> Unit)? = null,
    onLangClick: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    onReset: (() -> Unit)? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        onLangClick?.let { LangButton(onClick = it, tint = iconTint) }
        onToggleTheme?.let { ThemeToggleButton(tint = iconTint) }
        onReset?.let {
            ResetButton(
                onReset = it,
                soulColor = resetSoulColor,
                label = resetLabel,
                labelPosition = resetLabelPosition,
                dialogTitle = resetDialogTitle,
                dialogBody = resetDialogBody,
                dialogConfirmLabel = resetDialogConfirmLabel,
            )
        }

        onClose?.let {
            CloseButton(
                onClick = it,
                tint = iconTint,
                modifier = Modifier.height(64.dp)
            )
        }
    }
}
