package it.lcavagnari.pdm.dermcalc.ui.component.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalIsIdle
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.HeightInput
import it.lcavagnari.pdm.dermcalc.models.WeightInput
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulJustice
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulKindness

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightInputPicker(
    modifier: Modifier = Modifier,
    field: WeightInput,
    soulColor: Color = Color.Unspecified,
    onKilosChanged: (Int) -> Unit,
    onPoundsChanged: (Int) -> Unit
) {
    var openPicker by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isIdle = LocalIsIdle.current
    val currentIsIdle by rememberUpdatedState(isIdle)

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release && currentIsIdle) openPicker = true
        }
    }

    if (openPicker) {
        val wheelsKilos = listOf<SnapWheel<*>>(
            SnapWheel(
                items = (20..300).toList(),
                initialValue = field.value?.toInt()?.coerceIn(20, 300) ?: 70
            )
        )
        val wheelsPounds = listOf<SnapWheel<*>>(
            SnapWheel(
                items = (44..661).toList(),
                initialValue = field.value?.let {
                    field.kilosToPounds(it).toInt().coerceIn(44, 661)
                } ?: 154
            )
        )
        SnapWheelPickerDialog(
            title = R.string.picker_title_weight,
            wheels = if (field.isKilos) wheelsKilos else wheelsPounds,
            inputFieldLabels = listOf(R.string.label_weight),
            onDismiss = { openPicker = false },
            onConfirm = { values ->
                if (field.isKilos) onKilosChanged(values[0] as Int)
                else onPoundsChanged(values[0] as Int)
                openPicker = false
            }
        )
    }

    OutlinedTextField(
        value = field.value?.let {
            if (field.isKilos) "%.2f".format(it) + " kg" else "%.2f".format(field.kilosToPounds(it)) + " lb"
        } ?: "",
        onValueChange = {},
        modifier = modifier.semantics { testTag = "input_weight" },
        readOnly = true,
        label = { Text(stringResource(field.label), style = MaterialTheme.typography.labelMedium, color = soulColor) },
        placeholder = { Text(stringResource(R.string.placeholder_weight), style = MaterialTheme.typography.labelMedium) },
        trailingIcon = {
            IconButton(
                enabled = isIdle,
                onClick = { openPicker = true },
                modifier = Modifier.semantics { testTag = "btn_open_weight_picker" }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_weight_scale),
                    contentDescription = stringResource(R.string.cd_pick_weight)
                )
            }
        },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(17.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeightInputPicker(
    modifier: Modifier = Modifier,
    field: HeightInput,
    soulColor: Color = Color.Unspecified,
    onMetricChanged: (Int) -> Unit,
    onImperialChanged: (Pair<Int, Int>) -> Unit
) {
    var openPicker by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isIdle = LocalIsIdle.current
    val currentIsIdle by rememberUpdatedState(isIdle)

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release && currentIsIdle) openPicker = true
        }
    }

    if (openPicker) {
        val wheelsMetric = listOf<SnapWheel<*>>(
            SnapWheel(items = (50..272).toList(), initialValue = field.value?.toInt() ?: 170)
        )
        val wheelsImperial = listOf<SnapWheel<*>>(
            SnapWheel(
                items = (1..8).toList(),
                initialValue = field.value?.let { field.cmToFeetInches(it).first.toInt() } ?: 5
            ),
            SnapWheel(
                items = (0..11).toList(),
                initialValue = field.value?.let { field.cmToFeetInches(it).second.toInt() } ?: 0
            )
        )
        SnapWheelPickerDialog(
            title = R.string.picker_title_height,
            wheels = if (field.isMetric) wheelsMetric else wheelsImperial,
            inputFieldLabels = if (field.isMetric) listOf(R.string.label_height) else emptyList(),
            onDismiss = { openPicker = false },
            onConfirm = { values ->
                if (field.isMetric) onMetricChanged(values[0] as Int)
                else onImperialChanged(values[0] as Int to values[1] as Int)
                openPicker = false
            }
        )
    }

    OutlinedTextField(
        value = field.value?.let {
            if (field.isMetric) "${it.toInt()} cm" else field.cmToFeetInches(it)
                .let { (ft, inch) -> "${ft.toInt()} ft ${inch.toInt()} in" }
        } ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(field.label), style = MaterialTheme.typography.labelMedium, color = soulColor) },
        placeholder = { Text(stringResource(R.string.placeholder_height), style = MaterialTheme.typography.labelMedium) },
        trailingIcon = {
            IconButton(
                enabled = isIdle,
                onClick = { openPicker = true },
                modifier = Modifier.semantics { testTag = "btn_open_height_picker" }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_tape_measure),
                    contentDescription = stringResource(R.string.cd_pick_height)
                )
            }
        },
        interactionSource = interactionSource,
        modifier = modifier.semantics { testTag = "input_height" },
        shape = RoundedCornerShape(17.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary
        ),
    )
}
