package it.lcavagnari.pdm.dermcalc.screens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.DateInput
import it.lcavagnari.pdm.dermcalc.models.HeightInput
import it.lcavagnari.pdm.dermcalc.models.HeightMeasurements
import it.lcavagnari.pdm.dermcalc.models.InputField
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.Sex
import it.lcavagnari.pdm.dermcalc.models.SexInput
import it.lcavagnari.pdm.dermcalc.models.TextInput
import it.lcavagnari.pdm.dermcalc.models.WeightInput
import it.lcavagnari.pdm.dermcalc.models.WeightMeasurements
import it.lcavagnari.pdm.dermcalc.navigation.ProfileRoute as ProfileRouteDest
import it.lcavagnari.pdm.dermcalc.ui.portrait.MainPortraitActivity
import it.lcavagnari.pdm.dermcalc.utils.today

import java.util.Locale.getDefault

/**
 * Displays basic profile placeholder content centered on current screen.
 *
 * @param navController - controller available for future account flow routing.
 */
@Composable
fun ProfileRoute(navController: NavHostController, onboardingModel: OnboardingModel) {
    val inputFields by onboardingModel.fields.collectAsState()

    Column(
        modifier = Modifier.fillMaxHeight().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.65f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            inputFields.forEach { field ->
                Log.d("ProfileScreen", field.toString())
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 7.dp),
                        text = stringResource(field.label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = when (field) {
                            is TextInput -> field.value
                            is DateInput -> field.value?.toString()
                                ?: stringResource(R.string.placeholder_date)

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
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                HorizontalDivider(
                    thickness = 4.dp,
                    color = MaterialTheme.colorScheme.surface
                )
            }

        }

        UnitOfMeasurement(
            inputFields,
            onUpdateHeight = { onboardingModel.updateMeasurements(it) },
            onUpdateWeight = { onboardingModel.updateMeasurements(it) }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        Icon(
            modifier = Modifier.size(40.dp).padding(end = 5.dp),
            painter = painterResource(id = R.drawable.ic_annoying_dog),
            contentDescription = "annoying dog",
            tint = Color.Unspecified
        )
    }
}

@Composable
fun UnitOfMeasurement(inputFields:List<InputField>, onUpdateHeight:(it: HeightMeasurements) -> Unit, onUpdateWeight:(it: WeightMeasurements) -> Unit) {
    val heightInput: HeightInput = inputFields[3] as HeightInput
    val weightInput: WeightInput = inputFields[4] as WeightInput

    Text(
        stringResource(R.string.profile_measure_preference).uppercase(getDefault()),
        modifier = Modifier.padding(top = 10.dp),
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary,
        fontStyle = FontStyle.Italic
    )

    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 10.dp),
        shape = MaterialTheme.shapes.large,
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

            Text(
                text = stringResource(R.string.label_weight),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            SingleChoiceSegmentedButtonRow (
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                WeightMeasurements.entries.forEachIndexed { index, measurement ->
                    SegmentedButton(
                        selected = measurement == (if (weightInput.isKilos) WeightMeasurements.Kilos else WeightMeasurements.Pounds),
                        onClick = { onUpdateWeight(measurement) },
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
        }
    }
}


// DO NOT EDIT THIS

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val vm = remember { OnboardingModel().also { it.finishOnboarding() } }

    vm.updateName("Asriel ")
    vm.updateDateOfBirth(today().date)
    vm.updateHeightMetric(172)
    vm.updateWeightKilos(67)

    MainPortraitActivity(onboardingModel = vm, startingDestination = ProfileRouteDest)
}
