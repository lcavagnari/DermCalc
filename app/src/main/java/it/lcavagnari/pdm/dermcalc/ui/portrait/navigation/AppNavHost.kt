package it.lcavagnari.pdm.dermcalc.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import it.lcavagnari.pdm.dermcalc.screens.HomeScreen
import it.lcavagnari.pdm.dermcalc.screens.ProfileRoute
import it.lcavagnari.pdm.dermcalc.screens.ToolsScreen

@Composable
        /**
         * Registers destination composables and wires type-safe route navigation.
         *
         * @param modifier Modifier applied to the underlying [NavHost].
         * @param navController Controller managing back stack state transitions.
         * @param startDestination Initial route loaded when graph starts.
         * @return Unit.
         */
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
