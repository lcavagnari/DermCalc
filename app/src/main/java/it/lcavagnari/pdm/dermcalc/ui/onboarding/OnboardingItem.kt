package it.lcavagnari.pdm.dermcalc.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import it.lcavagnari.pdm.dermcalc.ui.component.input.DateInputPicker
import it.lcavagnari.pdm.dermcalc.ui.component.input.HeightInputPicker
import it.lcavagnari.pdm.dermcalc.ui.component.input.WeightInputPicker
import it.lcavagnari.pdm.dermcalc.ui.screens.OnboardingScreen
import it.lcavagnari.pdm.dermcalc.utils.today


/**
 * Renders the content of a single onboarding page, dispatching field updates to [OnboardingModel].
 *
 * @param page the [OnboardingScreen] descriptor for this page.
 * @param onboardingModel view model providing fields and receiving update events.
 */
@Composable
fun OnBoardItem(page: OnboardingScreen, onboardingModel: OnboardingModel) {
    val fields by onboardingModel.fields.collectAsState()
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

            key(field.id) {
            when (field) {
                is TextInput -> {
                    val focusRequester = remember { FocusRequester() }
                    val focusManager = LocalFocusManager.current
                    OutlinedTextField(
                        value = field.value,
                        onValueChange = { onboardingModel.updateName(it) },
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .semantics { testTag = "input_full_name" }
                            .focusRequester(focusRequester),
                        label = { Text(stringResource(field.label), style = MaterialTheme.typography.labelMedium) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
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
                        onDateSelected = { onboardingModel.updateDateOfBirth(it) },
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
                                onClick = { onboardingModel.updateSex(sex) },
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
                                onClick = { onboardingModel.updateMeasurements(measurement) },
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
                        onMetricChanged = { height: Int -> onboardingModel.updateHeightMetric(height) },
                        onImperialChanged = { height: Pair<Int, Int> ->
                            onboardingModel.updateHeightImperial(
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
                                onClick = { onboardingModel.updateMeasurements(measurement) },
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
                        onKilosChanged = { weight: Int -> onboardingModel.updateWeightKilos(weight) },
                        onPoundsChanged = { weight: Int -> onboardingModel.updateWeightPounds(weight) }
                    )

                    if (!field.isValid && field.isRequired && field.value != null) errorMessage =
                        stringResource(R.string.error_weight)
                    else if (field.value != null) successMessage = field.value.let {
                        if (field.isKilos) stringResource(R.string.weight_display_metric, it)
                        else stringResource(R.string.weight_display_imperial, field.kilosToPounds(it))
                    }
                }
            }
            }  // key(field.id)

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
