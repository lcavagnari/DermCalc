package it.lcavagnari.pdm.dermcalc.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.data.DateInput
import it.lcavagnari.pdm.dermcalc.data.HeightInput
import it.lcavagnari.pdm.dermcalc.data.HeightMeasurements
import it.lcavagnari.pdm.dermcalc.data.InputField
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.navigation.ProfileRoute
import it.lcavagnari.pdm.dermcalc.data.Sex
import it.lcavagnari.pdm.dermcalc.data.SexInput
import it.lcavagnari.pdm.dermcalc.data.TextInput
import it.lcavagnari.pdm.dermcalc.data.WeightInput
import it.lcavagnari.pdm.dermcalc.data.WeightMeasurements
import it.lcavagnari.pdm.dermcalc.data.isDefaultOrBlank
import it.lcavagnari.pdm.dermcalc.data.toLocalDate
import it.lcavagnari.pdm.dermcalc.ui.component.BorderSide
import it.lcavagnari.pdm.dermcalc.ui.component.BorderedCard
import it.lcavagnari.pdm.dermcalc.ui.component.input.InputFieldEditDialog
import it.lcavagnari.pdm.dermcalc.ui.preview.DermCalcPreview
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalDarkTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulKindness
import it.lcavagnari.pdm.dermcalc.utils.today


private val vm:(OnboardingModel) -> Unit = {
    it.finishOnboarding()
    it.updateDateOfBirth(today().date)
    it.updateHeightMetric(172)
    it.updateWeightKilos(67)
}
@Preview(showBackground = true) @Composable private fun ProfileScreenFullPreview() {
    DermCalcPreview(screen = ProfileRoute, setupOm = vm)
}
@Preview(showBackground = true) @Composable private fun ProfileScreenFullDarkPreview() {
    DermCalcPreview(darkTheme = true, setupOm = vm, screen = ProfileRoute)
}

/**
 * Main profile tab screen showing user details and unit preferences.
 */
@Composable
fun ProfileScreen(navController: NavHostController, onboardingModel: OnboardingModel) {
    val inputFields by onboardingModel.fields.collectAsState()
    val locale = LocalConfiguration.current.locales[0]

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            stringResource(R.string.profile_details).uppercase(locale),
            modifier = Modifier.padding(top = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
        )

        ProfileDetails(
            modifier = Modifier.padding(top = 10.dp, bottom = 30.dp),
            inputFields = inputFields,
            onboardingModel = onboardingModel
        )

        Text(
            stringResource(R.string.profile_measure_preference).uppercase(locale),
            modifier = Modifier.padding(top = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
        )

        UnitOfMeasurement(
            inputFields,
            onUpdateHeight = { onboardingModel.updateMeasurements(it) },
            onUpdateWeight = { onboardingModel.updateMeasurements(it) }
        )
    }

    if (LocalDarkTheme.current) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            Icon(
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 5.dp),
                painter = painterResource(id = R.drawable.ic_annoying_dog),
                contentDescription = "annoying dog",
                tint = Color.Unspecified
            )   
        }
    }
}

/**
 * BorderedCard listing all InputFields with edit icons that open InputFieldEditDialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetails(modifier: Modifier = Modifier, inputFields: List<InputField>, onboardingModel: OnboardingModel) {
    val locale = LocalConfiguration.current.locales[0]
    BorderedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        borderSide = BorderSide.Left,
        borderStrokeWidth = 3.dp,
        borderColor = SoulKindness,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        inputFields.forEachIndexed { index, field ->
            key(field.id) {
                var showDialog by remember { mutableStateOf(false) }

                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 5.dp, horizontal = 15.dp),
                        text = stringResource(field.label).uppercase(locale),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(Modifier.weight(1f))

                    Text(
                        modifier = Modifier
                            .padding(10.dp)
                            .height(20.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = if (field is DateInput && field.value == null) 16.sp else MaterialTheme.typography.bodyMedium.fontSize,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        softWrap = false,
                        text = when (field) {
                            is TextInput -> field.value
                            is DateInput -> field.value?.toString() ?: stringResource(R.string.placeholder_date)
                            is SexInput -> {
                                stringResource(
                                    when (field.value) {
                                        Sex.Male -> R.string.sex_male
                                        Sex.Female -> R.string.sex_female
                                        Sex.Other -> R.string.sex_other
                                        else -> R.string.sex_other
                                    }
                                )
                            }
                            is HeightInput -> field.value?.let {
                                if (field.isMetric) stringResource(R.string.height_display_metric, it.toInt())
                                else {
                                    val (feet, inches) = field.cmToFeetInches(it)
                                    stringResource(R.string.height_display_imperial, feet.toInt(), inches.toInt())
                                }
                            } ?: ""
                            is WeightInput -> field.value?.let {
                                if (field.isKilos) stringResource(R.string.weight_display_metric, it)
                                else stringResource(R.string.weight_display_imperial, field.kilosToPounds(it))
                            } ?: ""
                        }
                    )

                    val isDefault = field.isDefaultOrBlank()
                    val iconAlpha = if (isDefault) {
                        val transition = rememberInfiniteTransition()
                        val alpha by transition.animateFloat(
                            initialValue = 0.5f,
                            targetValue = 1.0f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200),
                                repeatMode = RepeatMode.Reverse
                            )
                        )
                        alpha
                    } else 1.0f

                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit ${field.id}",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = iconAlpha)
                        )
                    }
                }

                if (showDialog) {
                    InputFieldEditDialog(
                        field = field,
                        onDismiss = { showDialog = false },
                        onNameChanged = { onboardingModel.updateName(it); onboardingModel.persistFields() },
                        onDateChanged = { onboardingModel.updateDateOfBirth(it.toLocalDate()); onboardingModel.persistFields() },
                        onSexChanged = { onboardingModel.updateSex(it); onboardingModel.persistFields() },
                        onHeightMetricChanged = { onboardingModel.updateHeightMetric(it); onboardingModel.persistFields() },
                        onHeightImperialChanged = { ft, inch -> onboardingModel.updateHeightImperial(ft, inch); onboardingModel.persistFields() },
                        onWeightKilosChanged = { onboardingModel.updateWeightKilos(it); onboardingModel.persistFields() },
                        onWeightPoundsChanged = { onboardingModel.updateWeightPounds(it); onboardingModel.persistFields() }
                    )
                }

                if (index < inputFields.size - 1) HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(0.84f)
                        .padding(start = 15.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}

/**
 * BorderedCard with segmented button rows for height (metric/imperial) and weight (kg/lb) unit selection.
 */
@Composable
fun UnitOfMeasurement(
    inputFields: List<InputField>,
    onUpdateHeight: (HeightMeasurements) -> Unit,
    onUpdateWeight: (WeightMeasurements) -> Unit
) {
    val heightInput = inputFields[3] as HeightInput
    val weightInput = inputFields[4] as WeightInput

    BorderedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        shape = MaterialTheme.shapes.large,
        borderSide = BorderSide.Left,
        borderStrokeWidth = 3.dp,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.65f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = stringResource(R.string.label_height),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            SingleChoiceSegmentedButtonRow {
                HeightMeasurements.entries.forEachIndexed { index, measurement ->
                    SegmentedButton(
                        selected = measurement == (if (heightInput.isMetric) HeightMeasurements.Metric else HeightMeasurements.Imperial),
                        onClick = { onUpdateHeight(measurement) },
                        shape = SegmentedButtonDefaults.itemShape(index, HeightMeasurements.entries.size),
                        label = {
                            Text(stringResource(when (measurement) {
                                HeightMeasurements.Metric -> R.string.unit_metric
                                HeightMeasurements.Imperial -> R.string.unit_imperial
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

            Text(
                text = stringResource(R.string.label_weight),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                WeightMeasurements.entries.forEachIndexed { index, measurement ->
                    SegmentedButton(
                        selected = measurement == (if (weightInput.isKilos) WeightMeasurements.Kilos else WeightMeasurements.Pounds),
                        onClick = { onUpdateWeight(measurement) },
                        shape = SegmentedButtonDefaults.itemShape(index, WeightMeasurements.entries.size),
                        label = {
                            Text(stringResource(when (measurement) {
                                WeightMeasurements.Kilos -> R.string.unit_kilos
                                WeightMeasurements.Pounds -> R.string.unit_pounds
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
        }
    }
}
