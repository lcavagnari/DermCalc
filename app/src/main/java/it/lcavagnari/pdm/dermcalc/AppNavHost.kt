package it.lcavagnari.pdm.dermcalc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import it.lcavagnari.pdm.dermcalc.models.HeightInput
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.models.WeightInput
import it.lcavagnari.pdm.dermcalc.navigation.AppRoute
import it.lcavagnari.pdm.dermcalc.navigation.BMIToolRoute
import it.lcavagnari.pdm.dermcalc.navigation.BSAToolRoute
import it.lcavagnari.pdm.dermcalc.navigation.EASIToolRoute
import it.lcavagnari.pdm.dermcalc.navigation.HomeRoute
import it.lcavagnari.pdm.dermcalc.navigation.PASIToolRoute
import it.lcavagnari.pdm.dermcalc.navigation.ProfileRoute
import it.lcavagnari.pdm.dermcalc.navigation.ToolsRoute
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.BMIScreen
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.BSAScreen
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.EASIScreen
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.HomeScreen
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.PASIScreen
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.ProfileScreen
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.ToolsScreen


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
    startDestination: AppRoute
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable<HomeRoute> { HomeScreen(navController, quoteModel, onboardingModel, toolsModel) }
        composable<ToolsRoute> { ToolsScreen(navController, toolsModel) }
        composable<ProfileRoute> { ProfileScreen(navController, onboardingModel) }

        composable<BMIToolRoute> {
            val fields = onboardingModel.fields.collectAsState().value
            BMIScreen(
                heightInput = fields[3] as HeightInput,
                weightInput = fields[4] as WeightInput,
                onSaveResult = { result ->
                    toolsModel.addResult(result)
                    navController.popBackStack()
                }
            )
        }
        composable<BSAToolRoute> {
            BSAScreen(onSaveResult = { result ->
                toolsModel.addResult(result)
                navController.popBackStack()
            })
        }
        composable<PASIToolRoute> { PASIScreen() {} }
        composable<EASIToolRoute> { EASIScreen() {} }
    }
}