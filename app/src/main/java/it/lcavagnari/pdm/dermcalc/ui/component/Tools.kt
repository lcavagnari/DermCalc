package it.lcavagnari.pdm.dermcalc.ui.component

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.Severity
import it.lcavagnari.pdm.dermcalc.ui.theme.DermCalcTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.DeterminationMono
import it.lcavagnari.pdm.dermcalc.ui.theme.PixelSoft
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulJustice
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulKindness
import it.lcavagnari.pdm.dermcalc.ui.theme.severityColor

/**
 * Save button for calculator results with a two-step confirmation flow.
 *
 * The first tap arms the button and changes the label to a question; the second confirming tap
 * invokes [onSaveResult]. The armed state resets when the lifecycle reaches
 * [Lifecycle.Event.ON_STOP].
 *
 * @param modifier modifier applied to the full-width button.
 * @param enabled whether the button can be tapped.
 * @param soulColor accent [Color] used as the button container. Defaults to [SoulJustice].
 * @param onSaveResult callback invoked after the confirmation tap sequence completes.
 */
@Composable
fun ToolSaveButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    soulColor: Color = SoulJustice,
    onSaveResult: () -> Unit
) {
    val context = LocalContext.current
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

                Toast.makeText(context,
                    R.string.btn_saved_confirm,
                    Toast.LENGTH_SHORT
                ).show()
                onSaveResult()

            } else saveArmedTimes++
        }
    ) {
        Text(
            when {
                saveArmedTimes > 1 -> stringResource(R.string.btn_save)
                saveArmedTimes > 0 -> stringResource(R.string.btn_save) + "?"
                else -> stringResource(R.string.btn_confirm)
            },
            fontFamily = DeterminationMono
        )
    }
}

/**
 * Result summary card for a calculator score.
 *
 * Shows an optional tool label above a large score row; when [severity] is present, a compact
 * measurement-unit label appears beside the score and a colored severity badge is rendered below.
 *
 * @param modifier modifier applied to the bordered card.
 * @param soulColor accent [Color] used for the left border and score text.
 * @param toolLabel optional label displayed above the score.
 * @param toolMeasurementUnit unit label displayed beside the score when [severity] is present.
 * @param formattedScore preformatted score text to display prominently.
 * @param severity optional clinical [Severity] used to render the badge and severity color.
 */
@Composable
fun ToolResultCard(
    modifier: Modifier = Modifier,
    soulColor: Color,
    toolLabel: String?,
    toolMeasurementUnit: String,
    formattedScore: String,
    severity: Severity?
) {
    BorderedCard(
        modifier = modifier.fillMaxWidth(),
        borderSide = BorderSide.Left,
        borderColor = soulColor,
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
                    color = soulColor
                )
                if (severity != null) Text(
                    text = toolMeasurementUnit,
                    style = TextStyle(fontFamily = PixelSoft, fontSize = 16.sp),
                    fontWeight = FontWeight.Bold,
                    color = soulColor
                )
            }

            if (severity != null) ToolSeverityCard(severity = severity)
        }
    }
}


@Composable
fun ToolSeverityCard(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 13.sp,
    severity: Severity
) {
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
                Severity.NONE -> stringResource(R.string.severity_normal)
                Severity.MILD -> stringResource(R.string.severity_normal)
                Severity.MODERATE -> stringResource(R.string.severity_moderate)
                Severity.SEVERE -> stringResource(R.string.severity_severe)
            }.uppercase(),
            style = MaterialTheme.typography.bodySmall,
            fontFamily = PixelSoft,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ToolResultCardRegularPreview() {
    DermCalcTheme {
        ToolResultCard(
            soulColor = SoulKindness,
            toolLabel = "Your BMI",
            toolMeasurementUnit = stringResource(R.string.bmi_unit),
            formattedScore = "22.2",
            severity = Severity.MILD
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ToolSaveButtonRegularPreview() {
    DermCalcTheme {
        ToolSaveButton(
            enabled = true,
            soulColor = SoulJustice,
        ) { }
    }
}
