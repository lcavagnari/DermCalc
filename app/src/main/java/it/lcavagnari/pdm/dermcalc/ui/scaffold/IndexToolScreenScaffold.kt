package it.lcavagnari.pdm.dermcalc.ui.scaffold

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.BodyRegion
import it.lcavagnari.pdm.dermcalc.navigation.EASIToolRoute
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.Severity
import it.lcavagnari.pdm.dermcalc.ui.component.ToolResultCard
import it.lcavagnari.pdm.dermcalc.ui.component.ToolSaveButton
import it.lcavagnari.pdm.dermcalc.ui.component.input.BodyScan
import it.lcavagnari.pdm.dermcalc.ui.component.input.ResetButton
import it.lcavagnari.pdm.dermcalc.ui.preview.DermCalcPreview
import it.lcavagnari.pdm.dermcalc.utils.today
import kotlinx.coroutines.launch

// Setup for the preview viewmodel
private val vm:(OnboardingModel) -> Unit = {
    it.finishOnboarding()
    it.updateName("Alessandro Barbero ")
    it.updateDateOfBirth(today().date)
    it.updateHeightMetric(172)
    it.updateWeightKilos(67)
}

// EASI
@Preview(showBackground = true) @Composable private fun EASIScreenFullPreview() {
    DermCalcPreview(screen = EASIToolRoute, setupOm = vm)
}
@Preview(showBackground = true) @Composable private fun EASIScreenFullDarkPreview() {
    DermCalcPreview(screen = EASIToolRoute, darkTheme = true)
}

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
        if (!isLastPage)
            coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
    }
    val onBack = {
        if (!isFirstPage)
            coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 1. Header & Custom Step Progress Indicator
        ScafoldHeader(
            currentPage = pagerState.currentPage,
            soulColor = soulColor,
            formattedScore = formattedScore,
            toolMeasurementUnit = toolMeasurementUnit,
            onReset = {
                coroutineScope.launch { pagerState.animateScrollToPage(0) }
                onReset()
            },
        ) { coroutineScope.launch { pagerState.animateScrollToPage(it) } }


        // 2. Central Horizontal Pager for Page Content
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                userScrollEnabled = true
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) { pageContent(page, onNext, onBack) }
            }
        }


        // 3. Navigation Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp),
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
                Spacer(modifier = Modifier.width(10.dp))

                ToolSaveButton(
                    enabled = saveEnabled,
                    onSaveResult = onSaveResult,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 4. Final Result Card & Save Trigger
        AnimatedVisibility(visible = isLastPage && formattedScore != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                ToolResultCard(
                    soulColor = soulColor,
                    toolLabel = stringResource(R.string.tool_result_header, toolLabel).uppercase(),
                    toolMeasurementUnit = toolMeasurementUnit,
                    formattedScore = formattedScore ?: "--",
                    severity = severity
                )
            }
        }
    }
}

/**
 * Header card for index tool screens showing the current district name, a mini body diagram,
 * progress bar, score, and reset button.
 *
 * @param modifier modifier applied to the root container.
 * @param currentPage index of the current page/district being viewed.
 * @param soulColor the identity color theme for the specific calculator.
 * @param formattedScore live or final computed score string (e.g. "12.4").
 * @param toolMeasurementUnit measurement unit text (e.g. "/ 72").
 * @param onReset callback when the user requests a progress reset.
 * @param onPageSelect callback when the user selects a different page via the progress bar.
 */
@Composable
fun ScafoldHeader(
    modifier: Modifier = Modifier,
    currentPage: Int,
    soulColor: Color,
    formattedScore: String?,
    toolMeasurementUnit: String,
    onReset: () -> Unit,
    onPageSelect: (Int) -> Unit
) {
    val page = calculatorPages[currentPage]

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // District name
                Text(
                    stringResource(page.titleRes).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                BodyScan(
                    selectedRegions = page.bodyRegions,
                    size = Pair(40.dp, 60.dp),
                    soulColor = soulColor,
                    showHints = false
                )
            }

            ProgressBar(
                currentPage = currentPage,
                soulColor = soulColor,
                formattedScore = formattedScore,
                toolMeasurementUnit = toolMeasurementUnit,
                onReset = onReset,
                onPageSelect = onPageSelect
            )
        }
    }
}



@Composable
private fun ProgressBar(
    modifier: Modifier = Modifier,
    currentPage: Int,
    soulColor: Color,
    formattedScore: String?,
    toolMeasurementUnit: String,
    onReset: () -> Unit,
    onPageSelect: (Int) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        stringResource(
                            R.string.label_step_of,
                            currentPage + 1,
                            calculatorPages.size
                        ) + " ·",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        "${formattedScore ?: "--"} $toolMeasurementUnit",
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
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(segColor)
                                .clickable { onPageSelect(index) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            ResetButton(
                soulColor = soulColor,
                onReset = onReset,
                label = stringResource(R.string.btn_reset),
            )
        }
    }
}

/**
 * Row of tappable circles for selecting a severity score value (0 to max).
 *
 * @param modifier modifier applied to the root container.
 * @param value current selected score value.
 * @param max maximum score value that can be selected.
 * @param souldColor color theme for the score selector (note: spelling preserved).
 * @param onValueChange callback when the user selects a new score value.
 */
@Composable
fun ScoreSelector(
    modifier: Modifier = Modifier,
    value: Int,
    max: Int = 4,
    souldColor: Color = MaterialTheme.colorScheme.primary,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "0", modifier = modifier.padding(end = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))

        repeat(max + 1) { index ->
            Box(
                modifier = Modifier
                    .size(25.dp)
                    .clip(CircleShape)
                    .background(if (index <= value) souldColor else Color.Transparent)
                    .border(
                        2.dp,
                        if (index <= value) souldColor else MaterialTheme.colorScheme.outline,
                        CircleShape
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        onValueChange(index)
                    }
            )
        }

        Text(text = max.toString(), modifier = modifier.padding(end = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
    }
}
