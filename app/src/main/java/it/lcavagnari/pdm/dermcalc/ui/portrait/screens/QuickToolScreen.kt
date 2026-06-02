package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.BmiResult
import it.lcavagnari.pdm.dermcalc.models.BsaResult
import it.lcavagnari.pdm.dermcalc.models.HeightInput
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.models.WeightInput
import it.lcavagnari.pdm.dermcalc.navigation.BMIToolRoute
import it.lcavagnari.pdm.dermcalc.ui.portrait.MainPortraitActivity
import it.lcavagnari.pdm.dermcalc.ui.portrait.onboarding.HeightInputPicker
import it.lcavagnari.pdm.dermcalc.ui.portrait.onboarding.WeightInputPicker
import it.lcavagnari.pdm.dermcalc.ui.theme.DermCalcTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulBravery
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulPatience
import it.lcavagnari.pdm.dermcalc.ui.theme.bmiLabel
import it.lcavagnari.pdm.dermcalc.ui.theme.bmiSeverity
import it.lcavagnari.pdm.dermcalc.ui.theme.bsaSeverity
import it.lcavagnari.pdm.dermcalc.ui.theme.severityColor

@Composable
fun QuickToolScreen(
    modifier: Modifier = Modifier,
    soulColour: Color,
    saveEnabled: Boolean = true,
    onSaveResult: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.9f),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                content = content
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .semantics { testTag = "tool_btn_save" },
            enabled = saveEnabled,
            onClick = onSaveResult
        ) {
            Text(stringResource(R.string.btn_start))
        }
    }
}

@Composable
fun BMIScreen(
    modifier: Modifier = Modifier,
    heightInput: HeightInput,
    weightInput: WeightInput,
    onSaveResult: (BmiResult) -> Unit
) {
    var localHeight by remember { mutableStateOf(heightInput) }
    var localWeight by remember { mutableStateOf(weightInput) }
    val score = if (localHeight.value != null && localWeight.value != null)
        BmiResult.calculate(localWeight.value!!, localHeight.value!!)
    else null

    QuickToolScreen(
        modifier = modifier,
        soulColour = SoulPatience,
        saveEnabled = score != null,
        onSaveResult = { onSaveResult(BmiResult(localWeight.value!!, localHeight.value!!, score!!)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeightInputPicker(
                field = localHeight,
                onMetricChanged = { cm -> localHeight = localHeight.copy(value = cm.toDouble(), isValid = true) },
                onImperialChanged = { (feet, inches) -> localHeight = localHeight.copy(value = localHeight.feetInchesToCm(feet, inches), isValid = true) }
            )
            WeightInputPicker(
                field = localWeight,
                onKilosChanged = { kg -> localWeight = localWeight.copy(value = kg.toDouble(), isValid = true) },
                onPoundsChanged = { lb -> localWeight = localWeight.copy(value = localWeight.poundsToKilos(lb), isValid = true) }
            )
            if (score != null) {
                val severity = bmiSeverity(score)
                Text(
                    text = "%.1f".format(score),
                    style = MaterialTheme.typography.headlineLarge,
                    color = SoulPatience
                )
                Text(
                    text = bmiLabel(score),
                    style = MaterialTheme.typography.bodyMedium,
                    color = severityColor(severity)
                )
            }
        }
    }
}

@Composable
fun BSAScreen(modifier: Modifier = Modifier, onSaveResult: (BsaResult) -> Unit) {
    var percentage by remember { mutableFloatStateOf(0f) }

    QuickToolScreen(
        modifier = modifier,
        soulColour = SoulBravery,
        onSaveResult = {
            val pct = percentage.toDouble()
            onSaveResult(BsaResult(pct, pct))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Affected body surface area",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = percentage,
                onValueChange = { percentage = it },
                valueRange = 0f..100f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = SoulBravery,
                    activeTrackColor = SoulBravery
                )
            )
            Text(
                text = "%.1f %%".format(percentage),
                style = MaterialTheme.typography.bodyMedium
            )
            val severity = bsaSeverity(percentage.toDouble())
            Text(
                text = "%.1f".format(percentage),
                style = MaterialTheme.typography.headlineLarge,
                color = SoulBravery
            )
            Text(
                text = severity.name,
                style = MaterialTheme.typography.bodyMedium,
                color = severityColor(severity)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BMIScreenPreview() {
    BMIScreen(
        heightInput = HeightInput(id = "height", label = R.string.label_height, isMetric = true),
        weightInput = WeightInput(id = "weight", label = R.string.label_weight, isKilos = true)
    ) {}
}

@Preview(showBackground = true)
@Composable
fun BSAScreenPreview() {
    BSAScreen() { }
}

@Preview(showBackground = true)
@Composable
fun MainPortraitActivityPreview() {
    val context = LocalContext.current
    val app = object : Application() { init { attachBaseContext(context) } }
    val vm = remember { OnboardingModel(app) }.also { it.finishOnboarding() }
    val qm = remember { QuoteModel(app) }.also { it.updateQuote() }
    val tm = remember { ToolsModel(app) }
    DermCalcTheme {
        MainPortraitActivity(quoteModel = qm, onboardingModel = vm, toolsModel = tm, startingDestination = BMIToolRoute)
    }
}