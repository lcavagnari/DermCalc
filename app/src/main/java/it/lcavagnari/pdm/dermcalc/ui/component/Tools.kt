package it.lcavagnari.pdm.dermcalc.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.Severity
import it.lcavagnari.pdm.dermcalc.ui.theme.DeterminationMono
import it.lcavagnari.pdm.dermcalc.ui.theme.PixelSoft
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulJustice
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulKindness
import it.lcavagnari.pdm.dermcalc.ui.theme.severityColor

@Composable
fun ToolSaveButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    soulColor: Color = SoulJustice,
    onSaveResult: () -> Unit
) {
    var saveArmedTimes by remember { mutableStateOf(0) }

    // Reset
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        saveArmedTimes = 0
    }

    Button(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .semantics { testTag = "tool_btn_save" },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = soulColor,
        ),
        shape = RoundedCornerShape(3.dp),
        onClick = {
            if (saveArmedTimes >= 2) {
                saveArmedTimes = 0
                onSaveResult()

            } else saveArmedTimes++
        }
    ) {
        Text(if (saveArmedTimes > 0) stringResource(R.string.btn_save) else stringResource(R.string.btn_confirm), fontFamily = DeterminationMono)
    }
}

@Composable
fun ToolResultCard(
    modifier: Modifier = Modifier,
    soulColour: Color,
    toolLabel: String?,
    toolMeasurementUnit: String,
    formattedScore: String,
    severity: Severity?
) {
    BorderedCard(
        modifier = modifier.fillMaxWidth(),
        borderSide = BorderSide.Left,
        borderColor = soulColour,
        borderStrokeWidth = 2.dp,
        cornerRadius = 10.dp,
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (toolLabel != null) {
                Text(
                    text = toolLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = formattedScore,
                    style = TextStyle(fontFamily = DeterminationMono, fontSize = 56.sp),
                    color = soulColour
                )
                if (severity != null) Text(
                    text = toolMeasurementUnit,
                    style = TextStyle(fontFamily = PixelSoft, fontSize = 16.sp),
                    fontWeight = FontWeight.Bold,
                    color = soulColour
                )
            }

            if (severity != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = severityColor(severity),
                        contentColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = when (severity) {
                            Severity.Mild -> stringResource(R.string.severity_normal)
                            Severity.Moderate -> stringResource(R.string.severity_moderate)
                            Severity.Severe -> stringResource(R.string.severity_severe)
                        }.uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = PixelSoft,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultnPreview() {
    ToolResultCard(
        soulColour = SoulKindness,
        toolLabel = "Your BMI",
        toolMeasurementUnit = stringResource(R.string.bmi_unit),
        formattedScore = "22.2",
        severity = Severity.Mild
    )
}

@Preview(showBackground = true)
@Composable
fun ButtonPreview() {
    ToolSaveButton(
        enabled = true,
        soulColor = SoulJustice,
    ) { }
}