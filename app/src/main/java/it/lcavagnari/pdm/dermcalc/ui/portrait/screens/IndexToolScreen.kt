package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.Severity
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.navigation.EASIToolRoute
import it.lcavagnari.pdm.dermcalc.navigation.PASIToolRoute
import it.lcavagnari.pdm.dermcalc.ui.portrait.DermCalcPreview
import it.lcavagnari.pdm.dermcalc.ui.theme.soulFor
import it.lcavagnari.pdm.dermcalc.utils.today



// Setup for the preview viewmodel
private val vm:(OnboardingModel) -> Unit = {
    it.finishOnboarding()
    it.updateName("Asriel ")
    it.updateDateOfBirth(today().date)
    it.updateHeightMetric(172)
    it.updateWeightKilos(67)
}

// PASI
@Preview(showBackground = true) @Composable private fun PASIScreenFullPreview() {
    DermCalcPreview(screen = PASIToolRoute, setupOm = vm)
}
@Preview(showBackground = true) @Composable private fun PASIScreenFullDarkPreview() {
    DermCalcPreview(screen = PASIToolRoute, darkTheme = true)
}

// EASI
@Preview(showBackground = true) @Composable private fun EASIScreenFullPreview() {
    DermCalcPreview(screen = EASIToolRoute, setupOm = vm)
}
@Preview(showBackground = true) @Composable private fun EASIScreenFullDarkPreview() {
    DermCalcPreview(screen = EASIToolRoute, darkTheme = true)
}


/**
 * Placeholder screen for the PASI calculator.
 */
@Composable
fun PASIScreen(toolsModel: ToolsModel, onSaveResult: () -> Unit) {
    Text("PASI Screen Placeholder")
}


@Composable
fun ScoreSelector(
    modifier: Modifier = Modifier,
    maxValue: Int,
    range: Int = 4,
    onValueChange: (Int) -> Unit
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        for (i in 0..range) {
            val filled = i in 1..maxValue
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (filled) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .border(1.5.dp, if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, CircleShape)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onValueChange(i) }
            )
        }
    }
}

/**
 * Placeholder screen for the EASI calculator.
 */
@Composable
fun EASIScreen(toolsModel: ToolsModel, onSaveResult: () -> Unit) {
    val currentScore by toolsModel.easiDraftScore.collectAsState()
    val startPage by toolsModel.easiDraftPage.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = startPage,
        pageCount = { calculatorPages.size }
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            toolsModel.updateEasiDraft(currentScore, page)
        }
    }

    IndexToolScaffold(
        pages = calculatorPages,
        pagerState = pagerState,
        soulColor = soulFor("EASI").color,
        toolLabel = "EASI",
        toolMeasurementUnit = "/ 72",
        formattedScore = "%.1f".format(currentScore),
        severity = when {
            currentScore == 0.0 -> Severity.NONE
            currentScore < 7.0 -> Severity.MILD
            currentScore < 21.0 -> Severity.MODERATE
            else -> Severity.SEVERE
        },
        onReset = { toolsModel.resetEasiDraft() },
        onSaveResult = onSaveResult
    ) { pageIndex, onNext, _ ->
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Demonstration: ${stringResource(calculatorPages[pageIndex].titleRes)}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    toolsModel.updateEasiDraft(currentScore + 1.5, pageIndex)
                    onNext()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = soulFor("EASI").color
                )
            ) {
                Text("Simulate Input & Next")
            }
        }
    }
}

