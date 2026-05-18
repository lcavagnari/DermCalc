package it.lcavagnari.pdm.dermcalc.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.HomeScreen
import it.lcavagnari.pdm.dermcalc.screens.ProfileRoute
import it.lcavagnari.pdm.dermcalc.screens.ToolsScreen

/**
 * Registers destination composables and wires type-safe route navigation.
 *
 * @param modifier - modifier applied to the underlying [NavHost].
 * @param navController - controller managing back stack state transitions.
 * @param onboardingModel - view model forwarded to destinations that need user profile data.
 * @param toolsModel - view model forwarded to destinations that display or modify tool results.
 * @param quoteModel - view model forwarded to the home screen for quote display.
 * @param startDestination - initial route loaded when graph starts.
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
        composable<HomeRoute> {
            HomeScreen(navController, quoteModel, onboardingModel,toolsModel)

        }

        composable<ToolsRoute> {
            ToolsScreen(navController, toolsModel)
        }

        composable<ProfileRoute> {
            ProfileRoute(navController, onboardingModel)
        }
    }
}
