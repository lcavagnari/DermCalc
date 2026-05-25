package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.DateInput
import it.lcavagnari.pdm.dermcalc.models.HeightInput
import it.lcavagnari.pdm.dermcalc.models.HeightMeasurements
import it.lcavagnari.pdm.dermcalc.models.InputField
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.models.Sex
import it.lcavagnari.pdm.dermcalc.models.SexInput
import it.lcavagnari.pdm.dermcalc.models.TextInput
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.models.WeightInput
import it.lcavagnari.pdm.dermcalc.models.WeightMeasurements
import it.lcavagnari.pdm.dermcalc.models.toEpochMillis
import it.lcavagnari.pdm.dermcalc.models.toLocalDate
import it.lcavagnari.pdm.dermcalc.ui.component.BorderSide
import it.lcavagnari.pdm.dermcalc.ui.component.BorderedCard
import it.lcavagnari.pdm.dermcalc.ui.component.input.HeightPickerDialog
import it.lcavagnari.pdm.dermcalc.ui.component.input.WeightPickerDialog
import it.lcavagnari.pdm.dermcalc.ui.portrait.MainPortraitActivity
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalDarkTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulKindness
import it.lcavagnari.pdm.dermcalc.utils.today
import java.util.Locale.getDefault
import it.lcavagnari.pdm.dermcalc.navigation.ProfileRoute as ProfileRouteDest

/**
 * Profile tab. Lets the user view and edit every onboarding field after the initial setup.
 *
 * Layout (top → bottom):
 * - "DETAILS" section label.
 * - [ProfileDetails] card — one row per [InputField]: label, current value, and an edit [androidx.compose.material3.IconButton].
 *   Tapping the button opens a field-specific inline dialog:
 *     - [TextInput] → [androidx.compose.material3.AlertDialog] with an [androidx.compose.material3.OutlinedTextField].
 *     - [DateInput] → [androidx.compose.material3.DatePickerDialog].
 *     - [SexInput] → [androidx.compose.material3.AlertDialog] with a [androidx.compose.material3.SingleChoiceSegmentedButtonRow].
 *     - [HeightInput] / [WeightInput] → [it.lcavagnari.pdm.dermcalc.ui.component.input.SnapWheelPickerDialog].
 * - "MEASUREMENT PREFERENCE" section label.
 * - [UnitOfMeasurement] card — segmented buttons for metric/imperial height and kg/lb weight.
 * - Dark mode only: annoying-dog icon anchored to the bottom-end corner.
 *
 * All validation is delegated to [OnboardingModel] update methods; no local sanitisation.
 *
 * @param navController controller available for future account flow routing.
 * @param onboardingModel view model holding and validating all user profile fields.
 */
@Composable
fun ProfileScreen(navController: NavHostController, onboardingModel: OnboardingModel) {
    val inputFields by onboardingModel.fields.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            stringResource(R.string.profile_details).uppercase(getDefault()),
            modifier = Modifier.padding(top = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
        )

        ProfileDetails(
            modifier = Modifier.padding(top = 10.dp,bottom = 30.dp),
            inputFields = inputFields,
            onboardingModel = onboardingModel
        )

        Text(
            stringResource(R.string.profile_measure_preference).uppercase(getDefault()),
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

    if (LocalDarkTheme.current) Column(
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

/**
 * Drawer for the Profile details card
 *
 * TODO: Replace [onboardingModel] pass with parameter function call.
 * @param modifier Special modifications to the card
 * @param inputFields List of fields required during the onboarding process
 * @param onboardingModel TODO: to remove
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetails(modifier: Modifier = Modifier, inputFields:List<InputField>, onboardingModel: OnboardingModel) {
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
                        text = stringResource(field.label).uppercase(getDefault()),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(Modifier.weight(1f))

                    Text(
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = if (field is DateInput && field.value == null) 16.sp else MaterialTheme.typography.bodyMedium.fontSize,
                        color = MaterialTheme.colorScheme.onSurface,
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
                        }
                    )

                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit ${field.id}",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (showDialog) {
                    when (field) {
                        is TextInput -> {
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                title = { Text(stringResource(field.label)) },
                                text = {
                                    OutlinedTextField(
                                        value = field.value,
                                        onValueChange = { onboardingModel.updateName(it) },
                                        label = { Text(stringResource(field.label)) },
                                        singleLine = true,
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
                                        onClick = { showDialog = false },
                                        enabled = field.isValid,
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onPrimary,
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) { Text(stringResource(R.string.btn_ok)) }
                                },
                                dismissButton = {
                                    OutlinedButton(
                                        onClick = { showDialog = false },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onError,
                                            containerColor = MaterialTheme.colorScheme.error
                                        )
                                    ) { Text(stringResource(R.string.btn_cancel)) }
                                }
                            )
                        }

                        is DateInput -> {
                            val datePickerState = rememberDatePickerState(
                                initialSelectedDateMillis = field.value?.toEpochMillis()
                            )
                            LaunchedEffect(datePickerState.selectedDateMillis) {
                                datePickerState.selectedDateMillis?.let {
                                    onboardingModel.updateDateOfBirth(it.toLocalDate())
                                }
                            }
                            DatePickerDialog(
                                onDismissRequest = { showDialog = false },
                                confirmButton = {
                                    OutlinedButton(
                                        onClick = { showDialog = false },
                                        enabled = field.isValid,
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onPrimary,
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) { Text(stringResource(R.string.btn_ok)) }
                                },
                                dismissButton = {
                                    OutlinedButton(
                                        onClick = { showDialog = false },
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

                        is SexInput -> {
                            var selectedSex by remember(field.id) { mutableStateOf(field.value ?: Sex.Other) }
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                title = { Text(stringResource(field.label)) },
                                text = {
                                    SingleChoiceSegmentedButtonRow {
                                        Sex.entries.forEachIndexed { index, sex ->
                                            SegmentedButton(
                                                selected = selectedSex == sex,
                                                onClick = { selectedSex = sex },
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
                                },
                                confirmButton = {
                                    OutlinedButton(
                                        onClick = {
                                            onboardingModel.updateSex(selectedSex)
                                            showDialog = false
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onPrimary,
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) { Text(stringResource(R.string.btn_ok)) }
                                },
                                dismissButton = {
                                    OutlinedButton(
                                        onClick = { showDialog = false },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onError,
                                            containerColor = MaterialTheme.colorScheme.error
                                        )
                                    ) { Text(stringResource(R.string.btn_cancel)) }
                                }
                            )
                        }

                        is HeightInput -> {
                            HeightPickerDialog(
                                field = field,
                                onDismiss = { showDialog = false },
                                onMetricChanged = { onboardingModel.updateHeightMetric(it) },
                                onImperialChanged = { (ft, inch) -> onboardingModel.updateHeightImperial(ft, inch) }
                            )
                        }

                        is WeightInput -> {
                            WeightPickerDialog(
                                field = field,
                                onDismiss = { showDialog = false },
                                onKilosChanged = { onboardingModel.updateWeightKilos(it) },
                                onPoundsChanged = { onboardingModel.updateWeightPounds(it) }
                            )
                        }
                    }
                }

                if (index < inputFields.size) HorizontalDivider(
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
 * Unit of measurement preference menu.
 * Allows the user to change its preference in unit of measurement for height and weight
 *
 * @param inputFields List of fields required during onboarding
 * @param onUpdateHeight parameter function run on update of height preference
 * @param onUpdateWeight parameter function run on update of weight preference
 */
@Composable
fun UnitOfMeasurement(inputFields:List<InputField>, onUpdateHeight:(it: HeightMeasurements) -> Unit, onUpdateWeight:(it: WeightMeasurements) -> Unit) {
    val heightInput: HeightInput = inputFields[3] as HeightInput
    val weightInput: WeightInput = inputFields[4] as WeightInput

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
    val app = LocalContext.current.applicationContext as Application
    val vm = remember { OnboardingModel(app) }.also {
        it.finishOnboarding()
        it.updateName("Asriel ")
        it.updateDateOfBirth(today().date)
        it.updateHeightMetric(172)
        it.updateWeightKilos(67)
    }

    val qm = remember { QuoteModel(app) }.also { it.updateQuote() }
    val tm = remember { ToolsModel(app) }

    MainPortraitActivity(onboardingModel = vm, quoteModel = qm, toolsModel = tm, startingDestination = ProfileRouteDest)
}