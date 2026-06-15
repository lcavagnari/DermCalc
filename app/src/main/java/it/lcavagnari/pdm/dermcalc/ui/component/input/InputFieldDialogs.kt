package it.lcavagnari.pdm.dermcalc.ui.component.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.DateInput
import it.lcavagnari.pdm.dermcalc.models.HeightInput
import it.lcavagnari.pdm.dermcalc.models.InputField
import it.lcavagnari.pdm.dermcalc.models.Sex
import it.lcavagnari.pdm.dermcalc.models.SexInput
import it.lcavagnari.pdm.dermcalc.models.TextInput
import it.lcavagnari.pdm.dermcalc.models.WeightInput
import it.lcavagnari.pdm.dermcalc.models.toEpochMillis
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalIsIdle

/**
 * Unified modal switcher that handles dialog editing for any InputField type.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputFieldEditDialog(
    field: InputField,
    onDismiss: () -> Unit = {},
    onNameChanged: (String) -> Unit,
    onDateChanged: (Long) -> Unit,
    onSexChanged: (Sex) -> Unit,
    onHeightMetricChanged: (Int) -> Unit,
    onHeightImperialChanged: (Int, Int) -> Unit,
    onWeightKilosChanged: (Int) -> Unit,
    onWeightPoundsChanged: (Int) -> Unit
) {
    when (field) {
        is TextInput -> {
            TextInputDialog(
                field = field,
                onDismiss = onDismiss,
                onConfirm = { value ->
                    onNameChanged(value)
                    onDismiss()
                }
            )
        }
        is DateInput -> {
            DateInputDialog(
                field = field,
                onDismiss = onDismiss,
                onConfirm = { timestamp ->
                    onDateChanged(timestamp)
                    onDismiss()
                }
            )
        }
        is SexInput -> {
            SexInputDialog(
                field = field,
                onDismiss = onDismiss,
                onConfirm = { sex ->
                    onSexChanged(sex)
                    onDismiss()
                }
            )
        }
        is HeightInput -> {
            val wheelsMetric = listOf<SnapWheel<*>>(
                SnapWheel(items = (50..272).toList(), initialValue = field.value?.toInt() ?: 170)
            )
            val wheelsImperial = listOf<SnapWheel<*>>(
                SnapWheel(items = (1..8).toList(), initialValue = field.value?.let { field.cmToFeetInches(it).first.toInt() } ?: 5),
                SnapWheel(items = (0..11).toList(), initialValue = field.value?.let { field.cmToFeetInches(it).second.toInt() } ?: 0)
            )
            SnapWheelPickerDialog(
                title = R.string.picker_title_height,
                wheels = if (field.isMetric) wheelsMetric else wheelsImperial,
                inputFieldLabels = if (field.isMetric) listOf(R.string.label_height) else emptyList(),
                onDismiss = onDismiss,
                onConfirm = { values ->
                    if (field.isMetric) onHeightMetricChanged(values[0] as Int)
                    else onHeightImperialChanged(values[0] as Int, values[1] as Int)
                    onDismiss()
                }
            )
        }
        is WeightInput -> {
            val wheelsKilos = listOf<SnapWheel<*>>(
                SnapWheel(items = (20..300).toList(), initialValue = field.value?.toInt()?.coerceIn(20, 300) ?: 70)
            )
            val wheelsPounds = listOf<SnapWheel<*>>(
                SnapWheel(items = (44..661).toList(), initialValue = field.value?.let { field.kilosToPounds(it).toInt().coerceIn(44, 661) } ?: 154)
            )
            SnapWheelPickerDialog(
                title = R.string.picker_title_weight,
                wheels = if (field.isKilos) wheelsKilos else wheelsPounds,
                inputFieldLabels = listOf(R.string.label_weight),
                onDismiss = onDismiss,
                onConfirm = { values ->
                    if (field.isKilos) onWeightKilosChanged(values[0] as Int)
                    else onWeightPoundsChanged(values[0] as Int)
                    onDismiss()
                }
            )
        }
    }
}

/**
 * Displays a text input dialog for editing a [TextInput] field.
 * 
 * Renders an alert dialog with a text field pre-populated with the current field value.
 * 
 * @param modifier optional modifier for layout customization
 * @param field the [TextInput] field being edited
 * @param onDismiss callback invoked when the dialog is dismissed
 * @param onConfirm callback invoked when the user confirms the text input
 */
@Composable
fun TextInputDialog(
    modifier: Modifier = Modifier,
    field: TextInput,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var textValue by remember { mutableStateOf(field.value) }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text(stringResource(field.label)) },
        text = {
            LaunchedEffect(Unit) { focusRequester.requestFocus() }
            OutlinedTextField(
                value = textValue,
                onValueChange = { textValue = it },
                modifier = Modifier.focusRequester(focusRequester),
                label = { Text(stringResource(field.label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { if (textValue.isNotBlank() || !field.isRequired) onConfirm(textValue) }
                ),
                shape = RoundedCornerShape(17.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        confirmButton = {
            OutlinedButton(
                onClick = { onConfirm(textValue) },
                enabled = textValue.isNotBlank() || !field.isRequired,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) { Text(stringResource(R.string.btn_ok)) }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onError,
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) { Text(stringResource(R.string.btn_cancel)) }
        }
    )
}

/**
 * Displays a date input dialog for editing a [DateInput] field.
 * 
 * Renders a Material 3 date picker dialog with the current field value pre-selected.
 * 
 * @param field the [DateInput] field being edited
 * @param onDismiss callback invoked when the dialog is dismissed
 * @param onConfirm callback invoked when the user confirms the date selection
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateInputDialog(
    field: DateInput,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = field.value?.toEpochMillis()
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            OutlinedButton(
                onClick = { datePickerState.selectedDateMillis?.let { onConfirm(it) } },
                enabled = datePickerState.selectedDateMillis != null,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) { Text(stringResource(R.string.btn_ok)) }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onError,
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) { Text(stringResource(R.string.btn_cancel)) }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

/**
 * Displays a sex input dialog for editing a [SexInput] field.
 * 
 * Renders an alert dialog with segmented buttons for selecting biological sex.
 * 
 * @param field the [SexInput] field being edited
 * @param onDismiss callback invoked when the dialog is dismissed
 * @param onConfirm callback invoked when the user confirms the sex selection
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SexInputDialog(
    field: SexInput,
    onDismiss: () -> Unit,
    onConfirm: (Sex) -> Unit
) {
    var selectedSex by remember(field.id) { mutableStateOf(field.value ?: Sex.Other) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(field.label)) },
        text = {
            SingleChoiceSegmentedButtonRow {
                Sex.entries.forEachIndexed { index, sex ->
                    SegmentedButton(
                        selected = selectedSex == sex,
                        onClick = { selectedSex = sex },
                        shape = SegmentedButtonDefaults.itemShape(index, Sex.entries.size),
                        label = {
                            Text(stringResource(when (sex) {
                                Sex.Male -> R.string.sex_male
                                Sex.Female -> R.string.sex_female
                                Sex.Other -> R.string.sex_other
                            }))
                        },
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = MaterialTheme.colorScheme.primary,
                            activeContentColor = MaterialTheme.colorScheme.onPrimary,
                            inactiveContainerColor = Color.Transparent,
                            inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = { onConfirm(selectedSex) },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) { Text(stringResource(R.string.btn_ok)) }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onError,
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) { Text(stringResource(R.string.btn_cancel)) }
        }
    )
}

/**
 * Displays a weight input picker for editing a [WeightInput] field.
 * 
 * Renders a snap wheel picker dialog for selecting weight in kilograms or pounds.
 * 
 * @param modifier optional modifier for layout customization
 * @param field the [WeightInput] field being edited
 * @param soulColor color theme for the picker (unused in current implementation)
 * @param onKilosChanged callback invoked when kilograms value changes
 * @param onPoundsChanged callback invoked when pounds value changes
 */
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

/**
 * Displays a height input picker for editing a [HeightInput] field.
 * 
 * Renders a snap wheel picker dialog for selecting height in centimeters or feet/inches.
 * 
 * @param modifier optional modifier for layout customization
 * @param field the [HeightInput] field being edited
 * @param soulColor color theme for the picker (unused in current implementation)
 * @param onMetricChanged callback invoked when metric value changes
 * @param onImperialChanged callback invoked when imperial value changes
 */
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
