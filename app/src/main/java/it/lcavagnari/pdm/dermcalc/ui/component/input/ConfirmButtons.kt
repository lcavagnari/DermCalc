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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import it.lcavagnari.pdm.dermcalc.R

/**
 * A reusable confirmation dialog that implements a 3-tap confirmation sequence
 * (Confirm -> Action? -> Action) on its positive button.
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
        title = { Text(title, style = MaterialTheme.typography.headlineLarge, fontSize = 24.sp) },
        text = { Text(body, style = MaterialTheme.typography.labelMedium, fontSize = 16.sp) },
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

@Composable
fun ConfirmIconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    soulColor: Color,
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
            content(currentText, soulColor)
        }
    } else {
        Button(
            modifier = modifier,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(containerColor = soulColor),
            shape = RoundedCornerShape(3.dp),
            onClick = onClick
        ) {
            content(currentText, Color.Unspecified)
        }
    }
}
