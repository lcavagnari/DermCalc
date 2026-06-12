package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.Severity
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.navigation.EASIToolRoute
import it.lcavagnari.pdm.dermcalc.navigation.PASIToolRoute
import it.lcavagnari.pdm.dermcalc.ui.component.ToolResultCard
import it.lcavagnari.pdm.dermcalc.ui.component.ToolSaveButton
import it.lcavagnari.pdm.dermcalc.ui.portrait.DermCalcPreview
import it.lcavagnari.pdm.dermcalc.ui.theme.soulFor
import it.lcavagnari.pdm.dermcalc.utils.today
import kotlinx.coroutines.launch

/**
 * Descriptor representing a single step/district page in an index calculator.
 */
data class CalculatorPage(
    val titleRes: Int,
    val descriptionRes: Int? = null
)
/** Anatomical districts for calculators. */
val calculatorPages = listOf(
    CalculatorPage(R.string.district_head),
    CalculatorPage(R.string.district_upper_limbs),
    CalculatorPage(R.string.district_trunk),
    CalculatorPage(R.string.district_lower_limbs)
)

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
 * Shared scaffold for multipage index calculators (PASI, EASI).
 */
@Composable
private fun IndexToolScaffold(
    pages: List<CalculatorPage>,
    pagerState: PagerState,
    soulColor: Color,
    toolLabel: String,
    toolMeasurementUnit: String,
    formattedScore: String,
    severity: Severity,
    onReset: () -> Unit,
    onSaveResult: () -> Unit,
    content: @Composable (pageIndex: Int, onNext: () -> Unit, onPrev: () -> Unit) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.size - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Horizontal district indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            pages.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) soulColor
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                )
                if (index < pages.size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        // Calculation progress card (Score + Severity)
        ToolResultCard(
            soulColor = soulColor,
            toolLabel = toolLabel.uppercase(),
            toolMeasurementUnit = toolMeasurementUnit,
            formattedScore = formattedScore,
            severity = severity
        )

        // Reset and Save actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onReset,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(R.string.btn_reset).uppercase())
            }

            ToolSaveButton(
                modifier = Modifier.weight(1f),
                enabled = isLastPage,
                soulColor = soulColor,
                onSaveResult = onSaveResult
            )
        }

        // Page content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { index ->
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                content(
                    index,
                    {
                        if (index < pages.size - 1) {
                            coroutineScope.launch { pagerState.animateScrollToPage(index + 1) }
                        }
                    },
                    {
                        if (index > 0) {
                            coroutineScope.launch { pagerState.animateScrollToPage(index - 1) }
                        }
                    }
                )
            }
        }
    }
}




/**
 * Placeholder screen for the PASI calculator.
 */
@Composable
fun PASIScreen(toolsModel: ToolsModel, onSaveResult: () -> Unit) {
    Text("PASI Screen Placeholder")
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
            modifier = Modifier.fillMaxSize(),
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

