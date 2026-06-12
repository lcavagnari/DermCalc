package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.BodyScanModel
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.models.Severity
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.navigation.EASIToolRoute
import it.lcavagnari.pdm.dermcalc.ui.component.ToolResultCard
import it.lcavagnari.pdm.dermcalc.ui.component.ToolSaveButton
import it.lcavagnari.pdm.dermcalc.ui.portrait.DermCalcPreview
import it.lcavagnari.pdm.dermcalc.ui.portrait.MainPortraitActivity
import it.lcavagnari.pdm.dermcalc.ui.theme.DermCalcTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.soulFor
import kotlinx.coroutines.launch

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

    val onNext = {
        if (!isLastPage) {
            coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
        }
    }
    val onBack = {
        if (!isFirstPage) {
            coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Persistent Top Score & Progress Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = toolLabel.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = soulColor,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = stringResource(pages[pagerState.currentPage].titleRes),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(R.string.label_step_of, pagerState.currentPage + 1, pages.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = formattedScore ?: "--",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = soulColor
                            )
                            Text(
                                text = " $toolMeasurementUnit",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(0) }
                            onReset()
                        },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_reset_button),
                            contentDescription = stringResource(R.string.btn_reset),
                            tint = soulColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // 2. Custom Step Progress Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            pages.forEachIndexed { index, _ ->
                val isActive = index == pagerState.currentPage
                val hasPassed = index < pagerState.currentPage
                val dotColor = if (isActive) soulColor else if (hasPassed) soulColor.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)

                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(if (isActive) 24.dp else 6.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                        .clickable {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        }
                )
            }
        }

        // 3. Central Horizontal Pager for Page Content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = true
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    pages[page].descriptionRes?.let {
                        Text(
                            text = stringResource(it),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    pageContent(page, onNext, onBack)
                }
            }
        }

        // 4. Navigation Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous Button (disabled on page 0)
            OutlinedButton(
                onClick = onBack,
                enabled = !isFirstPage,
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(text = stringResource(R.string.btn_back))
            }

            // Next / Done Button
            if (!isLastPage) {
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = soulColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = stringResource(R.string.btn_next))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // 5. Final Result Card & Save Trigger
        AnimatedVisibility(visible = isLastPage && formattedScore != null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ToolResultCard(
                    soulColor = soulColor,
                    toolLabel = toolLabel.uppercase(),
                    toolMeasurementUnit = toolMeasurementUnit,
                    formattedScore = formattedScore ?: "--",
                    severity = severity
                )

                ToolSaveButton(
                    enabled = saveEnabled,
                    onSaveResult = onSaveResult,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Placeholder screen for the PASI calculator.
 *
 * Renders temporary text until the PASI input flow is implemented.
 *
 * @param toolsModel persistent view model for draft state.
 * @param onSaveResult callback reserved for saving a PASI result.
 */
@Composable
fun PASIScreen(toolsModel: ToolsModel, onSaveResult: () -> Unit) {
    Text("1234")
}

/**
 * Placeholder screen for the EASI calculator.
 *
 * Renders temporary text until the EASI input flow is implemented.
 *
 * @param toolsModel persistent view model for draft state.
 * @param onSaveResult callback reserved for saving an EASI result.
 */
@Composable
fun EASIScreen(toolsModel: ToolsModel, onSaveResult: () -> Unit) {
    // Persistent State: linked to ViewModel for cross-navigation persistence
    val currentScore by toolsModel.easiDraftScore.collectAsState()
    val startPage by toolsModel.easiDraftPage.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = startPage,
        pageCount = { easiPages.size }
    )

    // Automatically sync pager progress to ViewModel
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            toolsModel.updateEasiDraft(currentScore, page)
        }
    }

    IndexToolScaffold(
        pages = easiPages,
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
        // Example Page Content
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Demonstration: ${stringResource(easiPages[pageIndex].titleRes)}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    // Update persistent state in ViewModel
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

@Preview(showBackground = true)
@Composable
private fun PASIScreenFullPreview() {
    DermCalcPreview { _, _, tm, _ ->
        PASIScreen(toolsModel = tm, onSaveResult = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun PASIScreenFullDarkPreview() {
    DermCalcPreview(darkTheme = true) { _, _, tm, _ ->
        PASIScreen(toolsModel = tm, onSaveResult = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun PASIScreenRegularPreview() {
    val context = LocalContext.current
    val app = remember { object : Application() { init { attachBaseContext(context) } } }
    val tm = remember { ToolsModel(app) }
    
    DermCalcTheme {
        PASIScreen(toolsModel = tm, onSaveResult = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun PASIScreenRegularDarkPreview() {
    val context = LocalContext.current
    val app = remember { object : Application() { init { attachBaseContext(context) } } }
    val tm = remember { ToolsModel(app) }
    
    DermCalcTheme(darkTheme = true) {
        PASIScreen(toolsModel = tm, onSaveResult = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun EASIScreenFullPreview() {
    DermCalcPreview { _, _, tm, _ ->
        EASIScreen(toolsModel = tm, onSaveResult = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun EASIScreenFullDarkPreview() {
    DermCalcPreview(darkTheme = true) { _, _, tm, _ ->
        EASIScreen(toolsModel = tm, onSaveResult = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun EASIScreenRegularPreview() {
    val context = LocalContext.current
    val app = remember { object : Application() { init { attachBaseContext(context) } } }
    val tm = remember { ToolsModel(app) }
    
    DermCalcTheme {
        EASIScreen(toolsModel = tm, onSaveResult = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun EASIScreenRegularDarkPreview() {
    val context = LocalContext.current
    val app = remember { object : Application() { init { attachBaseContext(context) } } }
    val tm = remember { ToolsModel(app) }
    
    DermCalcTheme(darkTheme = true) {
        EASIScreen(toolsModel = tm, onSaveResult = {})
    }
}

/**
 * Descriptor representing a single step/district page in an index calculator.
 *
 * @property titleRes string resource id for the district label.
 * @property descriptionRes optional string resource id for patient-facing evaluation tips.
 */
data class CalculatorPage(
    val titleRes: Int,
    val descriptionRes: Int? = null
)

/** Anatomical districts for PASI calculator. */
val pasiPages = listOf(
    CalculatorPage(R.string.district_head),
    CalculatorPage(R.string.district_upper_limbs),
    CalculatorPage(R.string.district_trunk),
    CalculatorPage(R.string.district_lower_limbs)
)

/** Anatomical districts for EASI calculator. */
val easiPages = listOf(
    CalculatorPage(R.string.district_head),
    CalculatorPage(R.string.district_upper_limbs),
    CalculatorPage(R.string.district_trunk),
    CalculatorPage(R.string.district_lower_limbs)
)
