package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.BodyRegion
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.Severity
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.navigation.EASIToolRoute
import it.lcavagnari.pdm.dermcalc.navigation.PASIToolRoute
import it.lcavagnari.pdm.dermcalc.ui.component.ToolResultCard
import it.lcavagnari.pdm.dermcalc.ui.component.ToolSaveButton
import it.lcavagnari.pdm.dermcalc.ui.component.input.BodyScan
import it.lcavagnari.pdm.dermcalc.ui.portrait.DermCalcPreview
import it.lcavagnari.pdm.dermcalc.ui.theme.soulFor
import it.lcavagnari.pdm.dermcalc.utils.today
import kotlinx.coroutines.launch

/**
 * Descriptor representing a single step/district page in an index calculator.
 */
data class CalculatorPage(
    val titleRes: Int,
    val bodyRegions: List<BodyRegion> = listOf(BodyRegion.NONE)
)
/** Anatomical districts for calculators. */
val calculatorPages = listOf(
    CalculatorPage(R.string.district_head, listOf(BodyRegion.HEAD)),
    CalculatorPage(R.string.district_upper_limbs, listOf(BodyRegion.RIGHT_ARM, BodyRegion.LEFT_ARM)),
    CalculatorPage(R.string.district_trunk, listOf(BodyRegion.ANTERIOR_TRUNK, BodyRegion.POSTERIOR_TRUNK)),
    CalculatorPage(R.string.district_lower_limbs, listOf(BodyRegion.RIGHT_LEG, BodyRegion.LEFT_LEG))
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
 * Shared scaffold for multi-step index calculators (e.g. PASI, EASI).
 *
 * Provides a structured, page-by-page flow with:
 * - A persistent header displaying progress and running score.
 * - An animated step indicator representing the regions.
 * - A next/prev navigation bar.
 * - Integration with [ToolResultCard] and [ToolSaveButton] on the final step.
 *
 * @param modifier modifier applied to the root container.
 * @param pages list of descriptor objects for each step/district.
 * @param pagerState control state for page navigation.
 * @param soulColor the identity color theme for the specific calculator.
 * @param toolLabel title of the calculator (e.g. "PASI").
 * @param toolMeasurementUnit measurement unit text (e.g. "/ 72").
 * @param formattedScore live or final computed score string (e.g. "12.4").
 * @param severity computed severity category if applicable (e.g. [Severity.MODERATE]).
 * @param saveEnabled whether the final save button is active.
 * @param onReset callback when the user requests a progress reset.
 * @param onSaveResult callback when the user saves the finished calculation.
 * @param pageContent composable content block rendered for each step, providing the page index and navigation callbacks.
 */
@Composable
fun IndexToolScaffold(
    modifier: Modifier = Modifier,
    pages: List<CalculatorPage>,
    pagerState: PagerState = rememberPagerState(pageCount = { pages.size }),
    soulColor: Color,
    toolLabel: String,
    toolMeasurementUnit: String,
    formattedScore: String?,
    severity: Severity?,
    saveEnabled: Boolean = true,
    onReset: () -> Unit = {},
    onSaveResult: () -> Unit,
    pageContent: @Composable (pageIndex: Int, onNext: () -> Unit, onBack: () -> Unit) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex
    val isFirstPage = pagerState.currentPage == 0

    val page = pages[pagerState.currentPage]

    val onNext = {
        if (!isLastPage)
            coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
    }
    val onBack = {
        if (!isFirstPage)
            coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Persistent Top Score & Progress Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.5f
                )
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // District name
                    Text(
                        stringResource(page.titleRes),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    BodyScan(
                        selectedRegions = page.bodyRegions,
                        size = Pair(60.dp, 90.dp)
                    )
                }

                // 2. Custom Step Progress Indicator
                ProgressBar(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    currentPage = pagerState.currentPage,
                    onReset = onReset,
                    soulColor = soulColor,
                    formattedScore = formattedScore,
                    severity = severity,
                    toolMeasurementUnit = toolMeasurementUnit,
                    onPageSelect = { coroutineScope.launch { pagerState.animateScrollToPage(it) } }
                )
            }
        }
    }
}

/*

 */

@Composable
private fun ProgressBar(
    modifier: Modifier = Modifier,
    currentPage: Int,
    soulColor: Color,
    formattedScore: String?,
    toolMeasurementUnit: String,
    severity: Severity?,
    onReset: () -> Unit,
    onPageSelect: (Int) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            //
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    stringResource(R.string.label_step_of, currentPage + 1, calculatorPages.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "· ${formattedScore ?: "--"} $toolMeasurementUnit",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = soulColor
                )
            }


            // progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                calculatorPages.forEachIndexed { index, _ ->
                    val segColor = when {
                        index == currentPage -> soulColor
                        index < currentPage -> soulColor.copy(alpha = 0.5f)
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                    }

                    Box(
                        modifier = Modifier.weight(1f).height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(segColor).clickable { onPageSelect(index) }
                    )
                }
            }
        }


        Spacer(modifier = Modifier.width(12.dp))


        IconButton(
            modifier = Modifier.size(32.dp),
            onClick = onReset
        ) {
            Icon(
                painterResource(R.drawable.ic_reset_button),
                contentDescription = stringResource(R.string.btn_reset),
                tint = soulColor,
                modifier = Modifier.size(18.dp)
            )
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

