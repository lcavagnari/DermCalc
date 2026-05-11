package it.lcavagnari.pdm.dermcalc.ui.portrait

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.navigation.AppNavHost
import it.lcavagnari.pdm.dermcalc.navigation.BottomNavigationBar
import it.lcavagnari.pdm.dermcalc.navigation.navItems
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.OnboardingScreen
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.onboardingScreens



@Composable
        /**
         * Simple preview text composable used as starter template content.
         *
         * @param modifier Modifier applied to the text node.
         * @return Unit.
         */
public fun MainPortraitActivity(modifier: Modifier = Modifier, onboardingModel: OnboardingModel) {
    val hasSeenOnboarding by onboardingModel.hasSeenOnboarding.collectAsState()
    val navController = rememberNavController()
    val pagerState = rememberPagerState(pageCount = { onboardingScreens.size })

    Log.d("MainActivity", "-".repeat(50))
    Log.d(
        "MainActivity",
        "hasSeenOnboarding: ${onboardingModel.hasSeenOnboarding.collectAsState().value}"
    )
    Log.d("MainActivity", "-".repeat(50))

    if (!hasSeenOnboarding) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            OnboardingScreen(
                pagerState = pagerState,
                modifier = modifier.padding(innerPadding),
            ) { onboardingModel.finishOnboarding() }
        }


    } else Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                appItems = navItems
            )
        }

    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = modifier.padding(innerPadding)
        )
    }
}


private val vm = OnboardingModel()
/**
 * Design-time preview showing scaffold, bottom navigation, and start destination.
 *
 * @return Unit.
 */
@Preview(showBackground = true)
@Composable
fun MainPortraitActivityPreview() {
    MainPortraitActivity(onboardingModel = vm)
}
