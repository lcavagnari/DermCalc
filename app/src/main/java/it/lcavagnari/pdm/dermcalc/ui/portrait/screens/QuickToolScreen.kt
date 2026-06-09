package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.BmiResult
import it.lcavagnari.pdm.dermcalc.models.BsaResult
import it.lcavagnari.pdm.dermcalc.models.HeightInput
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.models.Severity
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.models.WeightInput
import it.lcavagnari.pdm.dermcalc.models.formattedScore
import it.lcavagnari.pdm.dermcalc.models.severity
import it.lcavagnari.pdm.dermcalc.navigation.BMIToolRoute
import it.lcavagnari.pdm.dermcalc.navigation.BSAToolRoute
import it.lcavagnari.pdm.dermcalc.ui.component.ToolResultCard
import it.lcavagnari.pdm.dermcalc.ui.component.ToolSaveButton
import it.lcavagnari.pdm.dermcalc.ui.component.input.BsaBodyDiagram
import it.lcavagnari.pdm.dermcalc.ui.component.input.HeightInputPicker
import it.lcavagnari.pdm.dermcalc.ui.component.input.WeightInputPicker
import it.lcavagnari.pdm.dermcalc.ui.portrait.MainPortraitActivity
import it.lcavagnari.pdm.dermcalc.ui.theme.DermCalcTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulBravery
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulPatience
import it.lcavagnari.pdm.dermcalc.ui.theme.onSoul


/**
 * BMI calculator screen with height and weight pickers.
 *
 * Computes a [BmiResult] once both measurements are present, displays the formatted score and
 * severity, and forwards the result through [onSaveResult] after save confirmation.
 *
 * @param modifier modifier applied to the quick-tool scaffold.
 * @param soulColor accent [Color] for BMI chrome. Defaults to [SoulPatience].
 * @param heightCm optional initial height in centimetres.
 * @param weightKg optional initial weight in kilograms.
 * @param onSaveResult callback invoked with the computed [BmiResult].
 */
@Composable
fun BMIScreen(
    modifier: Modifier = Modifier,
    soulColor: Color = SoulPatience,
    heightCm: Double? = null,
    weightKg: Double? = null,
    onSaveResult: (BmiResult) -> Unit,
) {
    var heightField by remember {
        mutableStateOf(
            HeightInput(id = "bmi_height", label = R.string.label_height).copy(
                value = heightCm, isValid = heightCm != null,
            )
        )
    }
    var weightField by remember {
        mutableStateOf(
            WeightInput(id = "bmi_weight", label = R.string.label_weight).copy(
                value = weightKg, isValid = weightKg != null,
            )
        )
    }

    val h = heightField.value
    val w = weightField.value
    val bmiResult = remember(h, w) {
        if (h != null && w != null) BmiResult.compute(w, h) else null
    }

    val formattedScore = bmiResult?.formattedScore() ?: "--"
    val severity = bmiResult?.severity()

    Scaffold(
        modifier = modifier,
        soulColor = soulColor,
        saveEnabled = bmiResult != null,
        toolLabel = stringResource(R.string.your_bmi, "BMI"),
        formattedScore = formattedScore,
        severity = severity,
        onSaveResult = { bmiResult?.let { onSaveResult(it) } }
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, start = 10.dp),
            text = stringResource(R.string.tool_measurement).uppercase(),
            color = onSoul(soulColor),
            letterSpacing = 2.sp,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(bottom = 20.dp, top = 5.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeightInputPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 5.dp),
                field = heightField,
                onMetricChanged = { cm ->
                    heightField = heightField.copy(value = cm.toDouble(), isValid = cm in 50..272)
                },
                onImperialChanged = { (feet, inches) ->
                    val cm = heightField.feetInchesToCm(feet, inches)
                    heightField = heightField.copy(value = cm, isValid = cm in 19.68..1207.08)
                }
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )

            WeightInputPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                field = weightField,
                onKilosChanged = { kg ->
                    weightField = weightField.copy(value = kg.toDouble(), isValid = kg in 20..300)
                },
                onPoundsChanged = { lb ->
                    weightField = weightField.copy(
                        value = weightField.poundsToKilos(lb),
                        isValid = lb in 44..661
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BMIScreenPreview() {
    BMIScreen(onSaveResult = { })
}


/**
 * Shared scaffold for quick calculator screens.
 *
 * Renders calculator input [content] inside a raised card, followed by an optional [ToolResultCard]
 * when [formattedScore] is provided and a [ToolSaveButton] at the bottom.
 *
 * @param modifier modifier applied to the root [Column].
 * @param soulColor accent [Color] used for result chrome.
 * @param saveEnabled whether the save button is enabled. Defaults to true.
 * @param toolLabel optional result label shown above the score.
 * @param formattedScore optional score text; when null, the result card is hidden.
 * @param severity optional [Severity] shown as a result badge.
 * @param onSaveResult callback invoked by the save button after confirmation.
 * @param content calculator-specific input content rendered inside the input card.
 */
@Composable
private fun Scaffold(
    modifier: Modifier = Modifier,
    soulColor: Color,
    saveEnabled: Boolean = true,
    toolLabel: String? = null,
    formattedScore: String? = null,
    severity: Severity? = null,
    onSaveResult: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            content = content
        )
        if (formattedScore != null) {
            ToolResultCard(
                soulColor = soulColor,
                toolLabel = toolLabel?.uppercase(),
                toolMeasurementUnit = stringResource(R.string.bmi_unit),
                formattedScore = formattedScore,
                severity = severity
            )
        }

        ToolSaveButton(
            enabled = saveEnabled,
            onSaveResult = onSaveResult
        )
    }
}

/**
 * BSA calculator screen backed by a 0–100 percentage slider.
 *
 * Displays the current affected body surface area as a [BsaResult] and forwards it through
 * [onSaveResult] after save confirmation.
 *
 * @param modifier modifier applied to the quick-tool scaffold.
 * @param onSaveResult callback invoked with the current [BsaResult].
 */
@Composable
fun BSAScreen(
    modifier: Modifier = Modifier,
    onSaveResult: (BsaResult) -> Unit,
) {
    var percentage by remember { mutableFloatStateOf(0f) }

    val bsaResult = remember(percentage) {

    }
    val bsaFormattedScore = bsaResult.formattedScore()
    val bsaSeverity = bsaResult.severity()

    Scaffold(
        modifier = modifier,
        soulColor = SoulBravery,
        formattedScore = bsaFormattedScore,
        severity = bsaSeverity,
        saveEnabled = percentage > 0f,
        onSaveResult = { onSaveResult(bsaResult) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
// Body diagram
            Card {
                Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    BsaBodyDiagram(state.regionValues, state.selectedRegion, vm::selectRegion)
                    Spacer(Modifier.height(8.dp))
                    Text("Front view · tap to select", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Slider(
                value = percentage,
                onValueChange = { percentage = it },
                valueRange = 0f..100f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = SoulBravery,
                    activeTickColor = SoulBravery
                )
            )
            Text(
                text = "%.1f %%".format(percentage),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainPortraitActivityPreview() {
    val context = LocalContext.current
    val app = object : Application() { init { attachBaseContext(context) } }
    val vm = remember { OnboardingModel(app) }.also { it.finishOnboarding(); it.updateWeightKilos(70); it.updateHeightMetric(172) }
    val qm = remember { QuoteModel(app) }.also { it.updateQuote() }
    val tm = remember { ToolsModel(app) }
    DermCalcTheme {
        MainPortraitActivity(quoteModel = qm, onboardingModel = vm, toolsModel = tm, startingDestination = BSAToolRoute)
    }
}

@Preview(showBackground = true)
@Composable
fun BSAScreenPreview() {
    BSAScreen(onSaveResult = {})
}


