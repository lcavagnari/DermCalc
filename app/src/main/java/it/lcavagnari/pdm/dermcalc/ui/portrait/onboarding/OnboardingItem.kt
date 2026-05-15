package it.lcavagnari.pdm.dermcalc.ui.portrait.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.DateInput
import it.lcavagnari.pdm.dermcalc.models.HeightInput
import it.lcavagnari.pdm.dermcalc.models.HeightMeasurements
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.Sex
import it.lcavagnari.pdm.dermcalc.models.SexInput
import it.lcavagnari.pdm.dermcalc.models.TextInput
import it.lcavagnari.pdm.dermcalc.models.WeightInput
import it.lcavagnari.pdm.dermcalc.models.WeightMeasurements
import it.lcavagnari.pdm.dermcalc.ui.shared.component.DateInputPicker
import it.lcavagnari.pdm.dermcalc.ui.shared.component.SnapWheel
import it.lcavagnari.pdm.dermcalc.ui.shared.component.SnapWheelPickerDialog
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.OnboardingScreen
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.onboardingScreens
import it.lcavagnari.pdm.dermcalc.utils.today


@Preview(showBackground = true)
@Composable
fun OnBoardItemPreview() {
    OnBoardItem(onboardingScreens[4])
}

/**
 * Renders the content of a single onboarding page, dispatching field updates to [OnboardingModel].
 *
 * @param page - the [OnboardingScreen] descriptor for this page.
 */
@Composable
fun OnBoardItem(page: OnboardingScreen) {
    val onBoardingModel: OnboardingModel = viewModel()
    val fields by onBoardingModel.fields.collectAsState()
    val pageFields =
        page.inputFieldIds.mapNotNull { fieldId -> fields.firstOrNull { it.id == fieldId } }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (page.imageRes != null) {
            Image(
                imageVector = page.imageRes,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .size(page.imageSize ?: 280.dp)
                    .padding(bottom = 30.dp)
            )

        } else if (page.imageDrawable != null) {
            Image(
                painter = painterResource(page.imageDrawable),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .height(300.dp)
                    .width(300.dp)
                    .padding(bottom = 25.dp)
            )
        }


        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )

        var errorMessage: String?
        var successMessage: String?

        // Sealed interface — adding a new InputField subtype will cause a compile error here,
        // forcing you to handle it before the build passes.
        pageFields.forEach { field ->
            errorMessage = null
            successMessage = null

            when (field) {
                is TextInput -> {
                    OutlinedTextField(
                        value = field.value,
                        onValueChange = { onBoardingModel.updateName(it) },
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .semantics { testTag = "input_full_name" },
                        label = { Text(stringResource(field.label), style = MaterialTheme.typography.labelMedium) },
                        shape = RoundedCornerShape(17.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    if (!field.isValid && field.isRequired && field.value.isNotBlank())
                        errorMessage = stringResource(R.string.error_name)
                    else if (field.value.isNotBlank()) successMessage =
                        stringResource(R.string.success_name, field.value)
                }

                is DateInput -> {
                    DateInputPicker(
                        field = field,
                        onDateSelected = { onBoardingModel.updateDateOfBirth(it) },
                    )

                    if (!field.isValid && field.isRequired && field.value != null)
                        errorMessage = stringResource(R.string.error_date)
                    else if (field.value != null) successMessage =
                        stringResource(R.string.age_display, today().year - field.value.year)
                }

                is SexInput -> {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.padding(top = 20.dp),
                    ) {

                        Sex.entries.forEachIndexed { index, sex ->
                            SegmentedButton(
                                selected = field.value == sex,
                                onClick = { onBoardingModel.updateSex(sex) },
                                shape = SegmentedButtonDefaults.itemShape(index, Sex.entries.size),
                                label = { Text(stringResource(when (sex) {
                                    Sex.Male -> R.string.sex_male
                                    Sex.Female -> R.string.sex_female
                                    Sex.Other -> R.string.sex_other
                                })) },
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = MaterialTheme.colorScheme.primary,
                                    activeContentColor = MaterialTheme.colorScheme.onPrimary,
                                    inactiveContainerColor = Color.Transparent,
                                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    if (!field.isValid && field.isRequired && field.value != null)
                        errorMessage = stringResource(R.string.error_sex)
                    else if (field.isRequired && field.value != null) successMessage =
                        stringResource(R.string.success_sex)
                }

                is HeightInput -> {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.padding(top = 30.dp),
                    ) {
                        HeightMeasurements.entries.forEachIndexed { index, measurement ->
                            SegmentedButton(
                                selected = measurement == (if (field.isMetric) HeightMeasurements.Metric else HeightMeasurements.Imperial),
                                onClick = { onBoardingModel.updateMeasurements(measurement) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index,
                                    HeightMeasurements.entries.size
                                ),
                                label = { Text(stringResource(when (measurement) {
                                    HeightMeasurements.Metric -> R.string.unit_metric
                                    HeightMeasurements.Imperial -> R.string.unit_imperial
                                })) },
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = MaterialTheme.colorScheme.primary,
                                    activeContentColor = MaterialTheme.colorScheme.onPrimary,
                                    inactiveContainerColor = Color.Transparent,
                                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    HeightInputPicker(
                        modifier = Modifier.padding(bottom = 40.dp),
                        field = field,
                        onMetricChanged = { height -> onBoardingModel.updateHeightMetric(height) },
                        onImperialChanged = { height ->
                            onBoardingModel.updateHeightImperial(
                                height.first,
                                height.second
                            )
                        }
                    )

                    if (!field.isValid && field.isRequired && field.value != null) errorMessage =
                        stringResource(R.string.error_height)
                    else if (field.value != null) successMessage = field.value.let {
                        if (field.isMetric) stringResource(R.string.height_display_metric, it.toInt())
                        else field.cmToFeetInches(it)
                            .let { (ft, inch) -> stringResource(R.string.height_display_imperial, ft.toInt(), inch.toInt()) }
                    }
                }

                is WeightInput -> {
                    SingleChoiceSegmentedButtonRow {
                        WeightMeasurements.entries.forEachIndexed { index, measurement ->
                            SegmentedButton(
                                selected = measurement == (if (field.isKilos) WeightMeasurements.Kilos else WeightMeasurements.Pounds),
                                onClick = { onBoardingModel.updateMeasurements(measurement) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index,
                                    WeightMeasurements.entries.size
                                ),
                                label = { Text(stringResource(when (measurement) {
                                    WeightMeasurements.Kilos -> R.string.unit_kilos
                                    WeightMeasurements.Pounds -> R.string.unit_pounds
                                })) },
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = MaterialTheme.colorScheme.primary,
                                    activeContentColor = MaterialTheme.colorScheme.onPrimary,
                                    inactiveContainerColor = Color.Transparent,
                                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    WeightInputPicker(
                        modifier = Modifier.padding(bottom = 30.dp),
                        field = field,
                        onKilosChanged = { weight -> onBoardingModel.updateWeightKilos(weight) },
                        onPoundsChanged = { weight -> onBoardingModel.updateWeightPounds(weight) }
                    )

                    if (!field.isValid && field.isRequired && field.value != null) errorMessage =
                        stringResource(R.string.error_weight)
                    else if (field.value != null) successMessage = field.value.let {
                        if (field.isKilos) stringResource(R.string.weight_display_metric, it)
                        else stringResource(R.string.weight_display_imperial, field.kilosToPounds(it))
                    }
                }
            }

            if (errorMessage != null) {
                Text(
                    errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium,
                    fontStyle = FontStyle.Italic
                )
            } else if (successMessage != null) {
                Text(
                    successMessage,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    fontStyle = FontStyle.Italic
                )
            }
        }

        if (page.description == null) return

        Text(
            text = stringResource(page.description),
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .wrapContentSize(Alignment.Center),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Outlined weight field that opens a [SnapWheelPickerDialog] on tap; switches between kg and lb wheels based on [WeightInput.isKilos].
 *
 * @param modifier - modifier applied to the [OutlinedTextField].
 * @param field - the [WeightInput] field supplying current value and unit preference.
 * @param onKilosChanged - callback invoked with the selected weight in whole kilograms.
 * @param onPoundsChanged - callback invoked with the selected weight in whole pounds.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightInputPicker(
    modifier: Modifier = Modifier,
    field: WeightInput,
    onKilosChanged: (Int) -> Unit,
    onPoundsChanged: (Int) -> Unit
) {
    val weightPickerWheelsKilos = listOf<SnapWheel<*>>(
        SnapWheel(
            items = (20..300).toList(),
            initialValue = field.value?.toInt()?.coerceIn(20, 300) ?: 70
        )
    )

    val weightPickerWheelsPounds = listOf<SnapWheel<*>>(
        SnapWheel(
            items = (44..661).toList(),
            initialValue = field.value?.let { field.kilosToPounds(it).toInt().coerceIn(44, 661) }
                ?: 154),
    )

    var openPicker by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) openPicker = true
        }
    }

    if (openPicker) {
        SnapWheelPickerDialog(
            title = R.string.picker_title_weight,
            // One wheel for both kilos and pounds.
            // The dialog returns List<Any?>; values are cast back to Int at this call site,
            // not inside the dialog, so the dialog stays generic.
            wheels = if (field.isKilos) weightPickerWheelsKilos else weightPickerWheelsPounds,
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
        label = { Text(stringResource(field.label), style = MaterialTheme.typography.labelMedium) },
        placeholder = { Text(stringResource(R.string.placeholder_weight)) },
        trailingIcon = {
            IconButton(
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
 * Outlined height field that opens a [SnapWheelPickerDialog] on tap; uses one wheel for metric (cm) and two wheels for imperial (feet + inches).
 *
 * @param modifier - modifier applied to the [OutlinedTextField].
 * @param field - the [HeightInput] field supplying current value and unit preference.
 * @param onMetricChanged - callback invoked with the selected height in whole centimetres.
 * @param onImperialChanged - callback invoked with a (feet, inches) pair.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeightInputPicker(
    modifier: Modifier = Modifier,
    field: HeightInput,
    onMetricChanged: (Int) -> Unit,
    onImperialChanged: (Pair<Int, Int>) -> Unit
) {
    val heightPickerWheelsMetric = listOf<SnapWheel<*>>(
        SnapWheel(
            items = (50..272).toList(),
            initialValue = field.value?.toInt() ?: 170
        )
    )

    val heightPickerWheelsImperial = listOf<SnapWheel<*>>(
        SnapWheel(items = (1..8).toList(), initialValue = field.value?.toInt() ?: 5),
        SnapWheel(items = (0..11).toList(), initialValue = field.value?.toInt() ?: 7)
    )

    var openPicker by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) openPicker = true
        }
    }

    if (openPicker) {
        SnapWheelPickerDialog(
            title = R.string.picker_title_height,
            // Two wheels for imperial (feet + inches), one for metric (cm).
            // The dialog returns List<Any?>; values are cast back to Int at this call site,
            // not inside the dialog, so the dialog stays generic.
            wheels = if (field.isMetric) heightPickerWheelsMetric else heightPickerWheelsImperial,
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
        label = { Text(stringResource(field.label), style = MaterialTheme.typography.labelMedium) },
        placeholder = { Text(stringResource(R.string.placeholder_height)) },
        trailingIcon = {
            IconButton(
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