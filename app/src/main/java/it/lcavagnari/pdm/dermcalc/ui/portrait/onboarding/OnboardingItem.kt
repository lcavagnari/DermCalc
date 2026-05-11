package it.lcavagnari.pdm.dermcalc.ui.portrait.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import it.lcavagnari.pdm.dermcalc.ui.portrait.component.DateInputPicker
import it.lcavagnari.pdm.dermcalc.ui.portrait.component.SnapWheel
import it.lcavagnari.pdm.dermcalc.ui.portrait.component.SnapWheelPickerDialog
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.OnboardingScreen
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.onboardingScreens
import it.lcavagnari.pdm.dermcalc.utils.today


@Preview(showBackground = true)
@Composable
fun OnBoardItemPreview() {
    OnBoardItem(onboardingScreens[4])
}

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
        page.imageRes?.let { image ->
            Image(
                imageVector = image,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .height(300.dp)
                    .width(300.dp)
                    .padding(bottom = 30.dp)
            )
        }

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
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
                        modifier = Modifier.padding(top = 20.dp),
                        label = { Text(field.label, style = MaterialTheme.typography.labelMedium) },
                        shape = RoundedCornerShape(17.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    if (!field.isValid && field.isRequired && field.value.isNotBlank())
                        errorMessage = "Please insert your full legal name."
                    else if (field.value.isNotBlank()) successMessage =
                        "Nice to meet you, ${field.value}!"
                }

                is DateInput -> {
                    DateInputPicker(
                        field = field,
                        onDateSelected = { onBoardingModel.updateDateOfBirth(it) },
                    )

                    if (!field.isValid && field.isRequired && field.value != null)
                        errorMessage = "Please pick a correct date."
                    else if (field.value != null) successMessage =
                        "(${today().year - field.value.year} years old)"
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
                                label = { Text(sex.name) },
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
                        errorMessage = "Please specify your gender from those supported."
                    else if (field.value != null) successMessage = "Thank you for sharing."
                }

                is HeightInput -> {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.padding(top = 20.dp),
                    ) {
                        HeightMeasurements.entries.forEachIndexed { index, measurement ->
                            SegmentedButton(
                                selected = measurement == (if (field.isMetric) HeightMeasurements.Metric else HeightMeasurements.Imperial),
                                onClick = { onBoardingModel.updateMeasurements(measurement) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index,
                                    HeightMeasurements.entries.size
                                ),
                                label = { Text(measurement.name) },
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
                        "Please double check your height."
                    else if (field.value != null) successMessage = field.value.let {
                        if (field.isMetric) "%.2f".format(it / 100) + " m"
                        else field.cmToFeetInches(it)
                            .let { (ft, inch) -> "${ft.toInt()} ft ${inch.toInt()} in" }
                    }
                }

                is WeightInput -> {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.padding(top = 20.dp),
                    ) {
                        WeightMeasurements.entries.forEachIndexed { index, measurement ->
                            SegmentedButton(
                                selected = measurement == (if (field.isKilos) WeightMeasurements.Kilos else WeightMeasurements.Pounds),
                                onClick = { onBoardingModel.updateMeasurements(measurement) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index,
                                    WeightMeasurements.entries.size
                                ),
                                label = { Text(measurement.name) },
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
                        modifier = Modifier.padding(bottom = 20.dp),
                        field = field,
                        onKilosChanged = { weight -> onBoardingModel.updateWeightKilos(weight) },
                        onPoundsChanged = { weight -> onBoardingModel.updateWeightPounds(weight) }
                    )

                    if (!field.isValid && field.isRequired && field.value != null) errorMessage =
                        "Please double check your weight."
                    else if (field.value != null) successMessage = field.value.let {
                        if (field.isKilos) "%.2f".format(it) + " kg" else "%.3f".format(
                            field.kilosToPounds(
                                it
                            )
                        ) + " lb"
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

        Text(
            text = page.description,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .wrapContentSize(Alignment.Center),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

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
            title = "Select weight",
            // One wheel for both kilos and pounds.
            // The dialog returns List<Any?>; values are cast back to Int at this call site,
            // not inside the dialog, so the dialog stays generic.
            wheels = if (field.isKilos) weightPickerWheelsKilos else weightPickerWheelsPounds,
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
        readOnly = true,
        label = { Text(field.label, style = MaterialTheme.typography.labelMedium) },
        placeholder = { Text("Select Weight") },
        trailingIcon = {
            IconButton(onClick = { openPicker = true }) {
                Icon(
                    painter = painterResource(R.drawable.ic_weight_scale),
                    contentDescription = "Pick weight"
                )
            }
        },
        interactionSource = interactionSource,
        modifier = modifier.padding(top = 20.dp),
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
            title = "Select height",
            // Two wheels for imperial (feet + inches), one for metric (cm).
            // The dialog returns List<Any?>; values are cast back to Int at this call site,
            // not inside the dialog, so the dialog stays generic.
            wheels = if (field.isMetric) heightPickerWheelsMetric else heightPickerWheelsImperial,
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
        label = { Text(field.label, style = MaterialTheme.typography.labelMedium) },
        placeholder = { Text("Insert Height") },
        trailingIcon = {
            IconButton(onClick = { openPicker = true }) {
                Icon(
                    painter = painterResource(R.drawable.ic_tape_measure),
                    contentDescription = "Pick height"
                )
            }
        },
        interactionSource = interactionSource,
        modifier = modifier.padding(top = 20.dp),
        shape = RoundedCornerShape(17.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary
        ),
    )
}