package it.lcavagnari.pdm.dermcalc.ui.component.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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

/**
 * Row of icon buttons displayed in the trailing area of [it.lcavagnari.pdm.dermcalc.ui.component.TopMenu] and the onboarding screen.
 *
 * @param modifier modifier applied to each icon box.
 * @param iconTint tint color applied to all icons.
 * @param onToggleTheme callback invoked when the theme-toggle icon is tapped.
 * @param onLangClick callback invoked when the language icon is tapped.
 */
@Composable
fun ButtonsTray(
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    onToggleTheme: () -> Unit,
    onLangClick: () -> Unit
) {
    val toggleDarkTheme = LocalToggleDarkTheme.current

    Box(
        modifier = modifier
            .size(35.dp)
            .padding(end = 5.dp)
            .clickable(onClick = onLangClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(20.dp).align(Alignment.Center),
            painter = painterResource(R.drawable.ic_lang_button),
            contentDescription = "Language",
            tint = iconTint
        )
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .clickable(onClick = { toggleDarkTheme() }),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            imageVector = if (LocalDarkTheme.current) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
            contentDescription = "Dark/Light mode",
            tint = iconTint
        )
    }
}

/**
 * Icon button with a reset icon and label that opens a confirmation dialog before firing [onReset].
 *
 * Uses the 3-tap confirm pattern via [ActionConfirmDialog] to prevent accidental resets.
 *
 * @param modifier modifier applied to the [IconButton].
 * @param toolLabel name of the tool being reset, shown in the confirmation body.
 * @param soulColor tint for the reset icon.
 * @param onReset callback invoked when the user confirms reset.
 */
@Composable
fun ResetButton(
    modifier: Modifier = Modifier,
    toolLabel: String = "",
    soulColor: Color,
    onReset: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ActionConfirmDialog(
            title = stringResource(R.string.reset_dialog_title),
            body = stringResource(R.string.reset_dialog_body, toolLabel),
            confirmLabel = stringResource(R.string.btn_reset),
            onConfirm = onReset,
            onDismiss = { showDialog = false }
        )
    }

    IconButton(
        modifier = modifier.height(64.dp),
        onClick = { showDialog = true }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_reset_button),
                contentDescription = stringResource(R.string.btn_reset),
                tint = soulColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = stringResource(R.string.btn_reset),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
