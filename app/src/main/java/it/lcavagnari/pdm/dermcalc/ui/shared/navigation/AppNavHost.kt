package it.lcavagnari.pdm.dermcalc.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import it.lcavagnari.pdm.dermcalc.screens.HomeScreen
import it.lcavagnari.pdm.dermcalc.screens.ProfileRoute
import it.lcavagnari.pdm.dermcalc.screens.ToolsScreen

/**
 * Registers destination composables and wires type-safe route navigation.
 *
 * @param modifier - modifier applied to the underlying [NavHost].
 * @param navController - controller managing back stack state transitions.
 * @param startDestination - initial route loaded when graph starts.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: AppRoute = HomeRoute
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable<HomeRoute> {
            HomeScreen(navController)

        }

        composable<ToolsRoute> {
            ToolsScreen(navController)
        }

        composable<ProfileRoute> {
            ProfileRoute(navController)
        }
    }
}
