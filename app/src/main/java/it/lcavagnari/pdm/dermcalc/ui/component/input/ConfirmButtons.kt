package it.lcavagnari.pdm.dermcalc.ui.component.input

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import it.lcavagnari.pdm.dermcalc.R

/**
 * A confirmation dialog whose positive button requires **three taps** to fire [onConfirm]:
 * tap 1 → label changes from "Confirm" to "$confirmLabel?"
 * tap 2 → label changes to [confirmLabel]
 * tap 3 → [onConfirm] fires and the dialog dismisses.
 * The armed state resets when the activity stops.
 */
@Composable
fun ActionConfirmDialog(
    title: String,
    body: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        text = { Text(body, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            ConfirmTextButton(
                labelDefault = stringResource(R.string.btn_confirm),
                labelArmed = "$confirmLabel?",
                labelExecute = confirmLabel,
                onConfirm = {
                    onConfirm()
                    onDismiss()
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_cancel))
            }
        }
    )
}

/**
 * A [TextButton] that requires **three taps** to confirm an action, using the same arm/disarm/execute
 * pattern as [ConfirmIconButton].
 *
 * @param labelDefault text shown before any tap.
 * @param labelArmed text shown after the first tap.
 * @param labelExecute text shown after the second tap, just before [onConfirm] fires.
 */
@Composable
fun ConfirmTextButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    labelDefault: String,
    labelArmed: String,
    labelExecute: String,
    onConfirm: () -> Unit
) {
    var armedTimes by remember { mutableStateOf(0) }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        armedTimes = 0
    }

    TextButton(
        modifier = modifier,
        onClick = {
            if (armedTimes >= 2) {
                armedTimes = 0
                onConfirm()
            } else {
                armedTimes++
            }
        },
        enabled = enabled
    ) {
        Text(
            text = when {
                armedTimes > 1 -> labelExecute
                armedTimes > 0 -> labelArmed
                else -> labelDefault
            }
        )
    }
}

/**
 * A button that requires **three taps** to confirm an action (1-tap arms, 2-tap shows execute label,
 * 3-tap fires [onConfirm]). The armed state resets when the activity stops ([Lifecycle.Event.ON_STOP]).
 *
 * @param color used as the icon tint when [isIconButton] is true, or the container color when false.
 * @param labelDefault text shown before any tap.
 * @param labelArmed text shown after the first tap (intended to indicate "are you sure?").
 * @param labelExecute text shown after the second tap, just before [onConfirm] fires.
 * @param isIconButton when true renders an [IconButton]; when false renders a filled [Button].
 * @param content composable receiving the current label and the tint/container [color].
 */
@Composable
fun ConfirmIconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color,
    labelDefault: String,
    labelArmed: String,
    labelExecute: String,
    onConfirm: () -> Unit,
    isIconButton: Boolean = false,
    content: @Composable (String, Color) -> Unit
) {
    var armedTimes by remember { mutableStateOf(0) }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        armedTimes = 0
    }

    val currentText = when {
        armedTimes > 1 -> labelExecute
        armedTimes > 0 -> labelArmed
        else -> labelDefault
    }

    val onClick: () -> Unit = {
        if (armedTimes >= 2) {
            armedTimes = 0
            onConfirm()
        } else {
            armedTimes++
        }
    }

    if (isIconButton) {
        IconButton(
            modifier = modifier,
            enabled = enabled,
            onClick = onClick
        ) {
            content(currentText, color)
        }
    } else {
        Button(
            modifier = modifier,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(containerColor = color),
            shape = RoundedCornerShape(3.dp),
            onClick = onClick
        ) {
            content(currentText, color)
        }
    }
}
