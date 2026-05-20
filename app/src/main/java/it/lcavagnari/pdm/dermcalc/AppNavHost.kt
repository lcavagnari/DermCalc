package it.lcavagnari.pdm.dermcalc

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
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
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.EASIToolRoute
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.HomeScreen
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.PASIToolRoute
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.ProfileScreen
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.ToolsScreen
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalDarkTheme


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
        composable<HomeRoute> {
            ApplyBackground { HomeScreen(navController, quoteModel, onboardingModel,toolsModel) }
        }

        composable<ToolsRoute> {
            ApplyBackground { ToolsScreen(navController, toolsModel) }
        }
        composable<ProfileRoute> {
            ApplyBackground { ProfileScreen(navController, onboardingModel) }
        }

        composable<BMIToolRoute> {
            ApplyBackground { BMIScreen(navController, toolsModel) }
        }
        composable<BSAToolRoute> {
            ApplyBackground { BSAScreen(navController, toolsModel) }
        }
        composable<PASIToolRoute> {
            ApplyBackground { PASIToolRoute(navController, toolsModel) }
        }
        composable<EASIToolRoute> {
            ApplyBackground { EASIToolRoute(navController, toolsModel) }
        }
    }
}


@Composable
private fun ApplyBackground(content: @Composable () -> Unit) {
    val dark = LocalDarkTheme.current

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(if (dark) R.drawable.bg_dark else R.drawable.bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        content()
    }
}
