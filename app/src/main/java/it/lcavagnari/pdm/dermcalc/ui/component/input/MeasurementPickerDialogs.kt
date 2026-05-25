package it.lcavagnari.pdm.dermcalc.ui.component.input

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.HeightInput
import it.lcavagnari.pdm.dermcalc.models.WeightInput

/**
 * [SnapWheelPickerDialog] pre-configured for height. Uses one cm wheel (metric) or two
 * wheels for feet + inches (imperial) based on [HeightInput.isMetric].
 *
 * @param field current [HeightInput] state supplying unit preference and existing value.
 * @param onDismiss called when the dialog is dismissed without confirming.
 * @param onMetricChanged called with the selected height in whole centimetres on confirm.
 * @param onImperialChanged called with a (feet, inches) pair on confirm.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeightPickerDialog(
    field: HeightInput,
    onDismiss: () -> Unit,
    onMetricChanged: (Int) -> Unit,
    onImperialChanged: (Pair<Int, Int>) -> Unit
) {
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
        onDismiss = onDismiss,
        onConfirm = { values ->
            if (field.isMetric) onMetricChanged(values[0] as Int)
            else onImperialChanged(values[0] as Int to values[1] as Int)
            onDismiss()
        }
    )
}

/**
 * [SnapWheelPickerDialog] pre-configured for weight. Switches between a kg wheel and a lb
 * wheel based on [WeightInput.isKilos].
 *
 * @param field current [WeightInput] state supplying unit preference and existing value.
 * @param onDismiss called when the dialog is dismissed without confirming.
 * @param onKilosChanged called with the selected weight in whole kilograms on confirm.
 * @param onPoundsChanged called with the selected weight in whole pounds on confirm.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightPickerDialog(
    field: WeightInput,
    onDismiss: () -> Unit,
    onKilosChanged: (Int) -> Unit,
    onPoundsChanged: (Int) -> Unit
) {
    val wheelsKilos = listOf<SnapWheel<*>>(
        SnapWheel(
            items = (20..300).toList(),
            initialValue = field.value?.toInt()?.coerceIn(20, 300) ?: 70
        )
    )
    val wheelsPounds = listOf<SnapWheel<*>>(
        SnapWheel(
            items = (44..661).toList(),
            initialValue = field.value?.let { field.kilosToPounds(it).toInt().coerceIn(44, 661) } ?: 154
        )
    )
    SnapWheelPickerDialog(
        title = R.string.picker_title_weight,
        wheels = if (field.isKilos) wheelsKilos else wheelsPounds,
        inputFieldLabels = listOf(R.string.label_weight),
        onDismiss = onDismiss,
        onConfirm = { values ->
            if (field.isKilos) onKilosChanged(values[0] as Int)
            else onPoundsChanged(values[0] as Int)
            onDismiss()
        }
    )
}
