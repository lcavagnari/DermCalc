package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.EasiScore
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.Severity
import it.lcavagnari.pdm.dermcalc.navigation.EASIToolRoute
import it.lcavagnari.pdm.dermcalc.navigation.PASIToolRoute
import it.lcavagnari.pdm.dermcalc.ui.component.input.BodyRegionSlider
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
fun PASIScreen(
    toolLabel: String = "PASI",
    score: Double = 0.0,
    startPage: Int = 0,
    onReset: () -> Unit,
    onSaveResult: () -> Unit
) {
    Text("PASI Screen Placeholder")
}


/**
 * Placeholder screen for the EASI calculator.
 */
@Composable
private fun SeverityRow(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f))
        ScoreSelector(value = value, max = 4, onValueChange = onValueChange)
    }
}

@Composable
fun EASIScreen(
    toolLabel: String = "EASI",
    score: Double = 0.0,
    startPage: Int = 0,
    saveEnabled: Boolean = true,
    onRegionScore: (Int) -> EasiScore,
    onScoreUpdate: (Int, EasiScore, Int) -> Unit,
    onReset: () -> Unit,
    onSaveResult: () -> Unit
) {
    val soulColor = soulFor(toolLabel).color

    val pagerState = rememberPagerState(
        initialPage = startPage,
        pageCount = { calculatorPages.size }
    )


    IndexToolScaffold(
        pages = calculatorPages,
        pagerState = pagerState,
        soulColor = soulColor,
        toolLabel = toolLabel,
        toolMeasurementUnit = "/ 72",
        formattedScore = "%.1f".format(score),
        severity = when {
            score == 0.0 -> Severity.NONE
            score < 7.0  -> Severity.MILD
            score < 21.0 -> Severity.MODERATE
            else         -> Severity.SEVERE
        },
        saveEnabled = saveEnabled,
        onReset = onReset,
        onSaveResult = onSaveResult
    ) { pageIndex, _, _ ->
        var draft by remember(pageIndex) { mutableStateOf(onRegionScore(pageIndex)) }

        val commit: (EasiScore) -> Unit = { updated ->
            draft = updated
            onScoreUpdate(pageIndex, updated, pagerState.currentPage)
        }

        val signs = listOf(
            Triple(R.string.easi_sign_erythema,        draft.erythema         ) { v: Int -> draft.copy(erythema = v) },
            Triple(R.string.easi_sign_induration,      draft.induration       ) { v: Int -> draft.copy(erythema = v) },
            Triple(R.string.easi_sign_excoriation,     draft.excoriation      ) { v: Int -> draft.copy(erythema = v) },
            Triple(R.string.easi_sign_lichenification, draft.lichenification  ) { v: Int -> draft.copy(erythema = v) },
        )

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            signs.forEach { (nameRes, currentScore, withScore) ->
                SeverityRow(stringResource(nameRes), currentScore) { commit(withScore(it)) }
            }

            BodyRegionSlider(
                modifier = Modifier.padding(vertical = 5.dp),
                soulColor = soulColor,
                region = calculatorPages[pageIndex].bodyRegions.first(),
                value = draft.area,
                onValueChange = { commit(draft.copy(area = it)) }
            )
        }
    }
}

