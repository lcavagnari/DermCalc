package it.lcavagnari.pdm.dermcalc.ui.portrait

import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import it.lcavagnari.pdm.dermcalc.AppNavHost
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.navigation.AppRoute
import it.lcavagnari.pdm.dermcalc.navigation.HomeRoute
import it.lcavagnari.pdm.dermcalc.navigation.NavigationBar
import it.lcavagnari.pdm.dermcalc.navigation.ProfileRoute
import it.lcavagnari.pdm.dermcalc.navigation.ToolsRoute
import it.lcavagnari.pdm.dermcalc.ui.component.TopMenu
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.OnboardingScreen
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.onboardingScreens

/**
 * Root portrait composable. Switches between the onboarding flow and the main app shell
 * based on [it.lcavagnari.pdm.dermcalc.models.OnboardingModel.hasSeenOnboarding].
 *
 * Two rendering paths:
 * - `!hasSeenOnboarding` → bare [androidx.compose.material3.Scaffold] (no top/bottom bars) containing [OnboardingScreen].
 * - `hasSeenOnboarding`  → [androidx.compose.material3.Scaffold] with [TopMenu] top bar,
 *   [it.lcavagnari.pdm.dermcalc.navigation.NavigationBar] bottom bar, and [it.lcavagnari.pdm.dermcalc.AppNavHost] content.
 *
 * @param modifier modifier applied to each scaffold's content slot.
 * @param onboardingModel view model providing onboarding state and field data.
 * @param toolsModel view model providing the list of stored [it.lcavagnari.pdm.dermcalc.models.ToolResult] entries.
 * @param quoteModel view model providing the currently displayed quote.
 * @param startingDestination initial navigation destination shown after onboarding completes.
 * @param onToggleTheme callback threaded through to [TopMenu] and [OnboardingScreen].
 */
@Composable
fun MainPortraitActivity(
    modifier: Modifier = Modifier,
    onboardingModel: OnboardingModel,
    toolsModel: ToolsModel,
    quoteModel: QuoteModel,
    startingDestination: AppRoute = HomeRoute,
    onToggleTheme: () -> Unit = {}
) {
    val hasSeenOnboarding by onboardingModel.hasSeenOnboarding.collectAsState()
    val navController = rememberNavController()
    val pagerState = rememberPagerState(pageCount = { onboardingScreens.size })

    // Show the onboarding flow until the user completes all pages.
    if (!hasSeenOnboarding) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            OnboardingScreen(
                pagerState = pagerState,
                modifier = modifier.padding(innerPadding),
                onFinish = { onboardingModel.finishOnboarding() },
                onToggleTheme = onToggleTheme
            )
        }


    } else Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopMenu(navController, onToggleTheme = onToggleTheme)
        },

        bottomBar = {
            NavigationBar(
                appItems = listOf(HomeRoute, ToolsRoute, ProfileRoute),
                navController = navController
            )
        }

    ) { innerPadding ->
        AppNavHost(
            modifier = modifier.padding(innerPadding),
            navController = navController,
            startDestination = startingDestination,
            onboardingModel = onboardingModel,
            toolsModel = toolsModel,
            quoteModel = quoteModel,
        )
    }
}


//  Preview

@Preview(showBackground = true)
@Composable
fun MainPortraitActivityPreview() {
    val app = LocalContext.current.applicationContext as Application
    val vm = remember { OnboardingModel(app) }.also { it.finishOnboarding() }
    val qm = remember { QuoteModel(app) }.also { it.updateQuote() }
    val tm = remember { ToolsModel(app) }
    MainPortraitActivity(quoteModel = qm, onboardingModel = vm, toolsModel = tm)
}
