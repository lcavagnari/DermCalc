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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.BmiResult
import it.lcavagnari.pdm.dermcalc.models.BsaResult
import it.lcavagnari.pdm.dermcalc.models.BodyScanModel
import it.lcavagnari.pdm.dermcalc.models.BsaRegion
import it.lcavagnari.pdm.dermcalc.models.HeightInput
import it.lcavagnari.pdm.dermcalc.models.Severity
import it.lcavagnari.pdm.dermcalc.models.WeightInput
import it.lcavagnari.pdm.dermcalc.models.formattedScore
import it.lcavagnari.pdm.dermcalc.models.severity
import it.lcavagnari.pdm.dermcalc.navigation.BMIToolRoute
import it.lcavagnari.pdm.dermcalc.navigation.BSAToolRoute
import it.lcavagnari.pdm.dermcalc.ui.component.ToolResultCard
import it.lcavagnari.pdm.dermcalc.ui.component.ToolSaveButton
import it.lcavagnari.pdm.dermcalc.ui.component.input.BsaBodyDiagram
import it.lcavagnari.pdm.dermcalc.ui.component.input.BsaRegionSlider
import it.lcavagnari.pdm.dermcalc.ui.component.input.HeightInputPicker
import it.lcavagnari.pdm.dermcalc.ui.component.input.WeightInputPicker
import it.lcavagnari.pdm.dermcalc.ui.portrait.DermCalcPreview
import it.lcavagnari.pdm.dermcalc.ui.theme.DermCalcTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulBravery
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulPatience
import it.lcavagnari.pdm.dermcalc.ui.theme.onSoul

private val vm:(BodyScanModel) -> Unit = {
    it.updateRegion(BsaRegion.HEAD, 10)
    it.updateRegion(BsaRegion.RIGHT_ARM, 20)
    it.updateRegion(BsaRegion.LEFT_ARM, 30)
}

@Preview(showBackground = true) @Composable private fun BMIScreenFullPreview() {
    DermCalcPreview(
        screen =    BMIToolRoute,
        setupOm = {
            it.finishOnboarding()
            it.updateWeightKilos(70)
            it.updateHeightMetric(172)
        }
    )
}
@Preview(showBackground = true) @Composable private fun BMIScreenFullDarkPreview() {
    DermCalcPreview(
        screen = BMIToolRoute,
        darkTheme = true,
        setupOm = {
            it.finishOnboarding()
            it.updateWeightKilos(70)
            it.updateHeightMetric(172)
        }
    )
}

@Preview(showBackground = true) @Composable private fun BSAScreenFullPreview() {
    DermCalcPreview(screen = BSAToolRoute, setupBm = vm)
}
@Preview(showBackground = true) @Composable private fun BSAScreenFullDarkPreview() {
    DermCalcPreview(darkTheme = true, screen = BSAToolRoute, setupBm = vm)
}

/**
 * BMI calculator screen with height and weight pickers.
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
                value = heightCm, isValid = heightCm != null
            )
        )
    }
    var weightField by remember {
        mutableStateOf(
            WeightInput(id = "bmi_weight", label = R.string.label_weight).copy(
                value = weightKg, isValid = weightKg != null
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
        toolMeasurementUnit = stringResource(R.string.bmi_unit),
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
                onMetricChanged = { cm: Int ->
                    heightField = heightField.copy(value = cm.toDouble(), isValid = cm in 50..272)
                },
                onImperialChanged = { pair: Pair<Int, Int> ->
                    val (feet, inches) = pair
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
                onKilosChanged = { kg: Int ->
                    weightField = weightField.copy(value = kg.toDouble(), isValid = kg in 20..300)
                },
                onPoundsChanged = { lb: Int ->
                    weightField = weightField.copy(
                        value = weightField.poundsToKilos(lb),
                        isValid = lb in 44..661
                    )
                }
            )
        }
    }
}

/**
 * Shared scaffold for quick calculator screens.
 */
@Composable
private fun Scaffold(
    modifier: Modifier = Modifier,
    soulColor: Color,
    saveEnabled: Boolean = true,
    toolLabel: String? = null,
    toolMeasurementUnit: String,
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
                toolMeasurementUnit = toolMeasurementUnit,
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

@Composable
fun BSAScreen(
    modifier: Modifier = Modifier,
    soulColor: Color = SoulBravery,
    vm: BodyScanModel,
    onSaveResult: (BsaResult) -> Unit,
) {
    val state by vm.state.collectAsState()
    val selectedRegion = state.selectedRegion
    val result = state.result

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { vm.reset() }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card {
            Column(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BsaBodyDiagram(
                    selectedRegion = selectedRegion,
                    regionValues = state.regionValues,
                    onRegionSelected = vm::selectRegion,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.bsa_diagram_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        BsaRegionSlider(
            region = selectedRegion,
            value = state.regionValues[selectedRegion] ?: 0,
            onValueChange = { vm.updateRegion(selectedRegion, it) },
        )

        if (result.score > 0.0) {
            ToolResultCard(
                soulColor = SoulBravery,
                toolLabel = stringResource(R.string.tools_bsa).uppercase(),
                toolMeasurementUnit = stringResource(R.string.bsa_unit),
                formattedScore = result.formattedScore(),
                severity = result.severity()
            )
        }


        ToolSaveButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = result.isValid()
        ) { onSaveResult(result) }

    }
}
