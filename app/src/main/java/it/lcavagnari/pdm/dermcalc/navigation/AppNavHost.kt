package it.lcavagnari.pdm.dermcalc.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.BodyScanModel
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.ui.scaffold.calculatorPages
import it.lcavagnari.pdm.dermcalc.ui.screens.BMIScreen
import it.lcavagnari.pdm.dermcalc.ui.screens.BSAScreen
import it.lcavagnari.pdm.dermcalc.ui.screens.EASIScreen
import it.lcavagnari.pdm.dermcalc.ui.screens.HomeScreen
import it.lcavagnari.pdm.dermcalc.ui.screens.PASIScreen
import it.lcavagnari.pdm.dermcalc.ui.screens.ProfileScreen
import it.lcavagnari.pdm.dermcalc.ui.screens.ToolsScreen
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalIsIdle
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalNavigate
import kotlinx.coroutines.flow.MutableStateFlow


/**
 * Registers destination composables and wires type-safe route navigation.
 *
 * @param modifier modifier applied to the underlying [NavHost].
 * @param navController controller managing back stack state transitions.
 * @param onboardingModel view model forwarded to destinations that need user profile data.
 * @param toolsModel view model forwarded to destinations that display or modify tool results.
 * @param quoteModel view model forwarded to the home screen for quote display.
 * @param startDestination initial route loaded when graph starts.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onboardingModel: OnboardingModel,
    toolsModel: ToolsModel,
    quoteModel: QuoteModel,
    bodyScanModel: BodyScanModel,
    startDestination: AppRoute
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val lifecycleState by remember(navBackStackEntry) {
        navBackStackEntry?.lifecycle?.currentStateFlow ?: MutableStateFlow(Lifecycle.State.RESUMED)
    }.collectAsState()
    val isIdle = lifecycleState == Lifecycle.State.RESUMED

    CompositionLocalProvider(
        LocalNavigate provides { route -> if (isIdle) navController.navigate(route) },
        LocalIsIdle provides isIdle
    ) {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination
        ) {
            composable<HomeRoute> { HomeScreen(quoteModel, onboardingModel, toolsModel) }
            composable<ToolsRoute> { ToolsScreen(toolsModel) }
            composable<ProfileRoute> { ProfileScreen(navController, onboardingModel) }

            composable<BMIToolRoute> {
                BMIScreen(
                    heightCm = onboardingModel.heightInput.value,
                    weightKg = onboardingModel.weightInput.value,
                    isMetric = onboardingModel.heightInput.isMetric,
                    isKilos = onboardingModel.weightInput.isKilos,
                    onSaveResult = { result ->
                        if (toolsModel.addResult(result))
                            navController.popBackStack()
                    }
                )
            }
            composable<BSAToolRoute> {
                BSAScreen(
                    vm = bodyScanModel,
                    onSaveResult = { result ->
                        navController.saveWithFeedback(
                            onSave = { toolsModel.addResult(result) },
                            onReset = {}
                        )
                    }
                )
            }
            composable<PASIToolRoute> {
                LaunchedEffect(toolsModel) {
                    toolsModel.initPasiDraft(calculatorPages.size)
                }

                val pasiScore by toolsModel.pasiScore.collectAsState()
                val pasiHasData by toolsModel.pasiHasData.collectAsState()

                PASIScreen(
                    score = pasiScore,
                    saveEnabled = pasiHasData,
                    startPage = toolsModel.pasiDraftPage,
                    onRegionScore = toolsModel::pasiRegionScore,
                    onScoreUpdate = toolsModel::updatePasiDraft,
                    onReset = toolsModel::resetPasiDraft,
                    onSaveResult = {
                        navController.saveWithFeedback(
                            onSave = toolsModel::savePasiDraft,
                            onReset = toolsModel::resetPasiDraft
                        )
                    }
                )
            }
            composable<EASIToolRoute> {
                LaunchedEffect(toolsModel) {
                    toolsModel.initEasiDraft(calculatorPages.size)
                }

                val easiScore by toolsModel.easiScore.collectAsState()
                val easiHasData by toolsModel.easiHasData.collectAsState()

                EASIScreen(
                    score = easiScore,
                    saveEnabled = easiHasData,
                    startPage = toolsModel.easiDraftStartPage,
                    onRegionScore = toolsModel::easiRegionScore,
                    onScoreUpdate = toolsModel::updateEasiDraft,
                    onReset = toolsModel::resetEasiDraft,
                    onSaveResult = {
                        navController.saveWithFeedback(
                            onSave = toolsModel::saveEasiDraft,
                            onReset = toolsModel::resetEasiDraft
                        )
                    }
                )
            }
        }
    }
}

/**
 * Attempts to save via [onSave]; on success resets via [onReset] and pops the back stack.
 * Shows a "Failed to save" toast if [onSave] returns `false`.
 */
private fun NavHostController.saveWithFeedback(
    onSave: () -> Boolean,
    onReset: () -> Unit
) {
    val ctx = context
    if (onSave()) {
        onReset()
        popBackStack()
        Toast.makeText(ctx, R.string.btn_saved_confirm, Toast.LENGTH_SHORT).show()
    } else Toast.makeText(ctx, R.string.error_save_failed, Toast.LENGTH_SHORT).show()

}

