package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.HeightInput
import it.lcavagnari.pdm.dermcalc.models.WeightInput
import it.lcavagnari.pdm.dermcalc.ui.portrait.onboarding.HeightInputPicker
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulPatience

/** Placeholder screen for quick calculators. Not yet implemented. */
@Composable
fun QuickToolScreen(
    modifier: Modifier = Modifier,
    soulColour: Color,
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
            enabled = true,
            onClick = onSaveResult
        ) {
            Text(stringResource(R.string.btn_start))
        }
    }
}

/** Placeholder screen for the BMI calculator. Not yet implemented. */
@Composable
fun BMIScreen(
    modifier: Modifier = Modifier,
    heightInput: HeightInput,
    weightInput: WeightInput,
    onSaveResult: () -> Unit
) {
    // This is a placeholder
    QuickToolScreen(
        modifier,
        soulColour = SoulPatience,
        onSaveResult = onSaveResult,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeightInputPicker(
                field = heightInput,
                onMetricChanged = {},
                onImperialChanged = {}
            )
        }
    }
}

/** Placeholder screen for the BSA calculator. Not yet implemented. */
@Composable
fun BSAScreen(modifier: Modifier = Modifier, onSaveResult: () -> Unit) {
    QuickToolScreen(
        modifier,
        soulColour = SoulPatience,
        onSaveResult = onSaveResult,
    ) { }
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