package it.lcavagnari.pdm.dermcalc.ui.portrait

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import it.lcavagnari.pdm.dermcalc.navigation.AppRoute
import it.lcavagnari.pdm.dermcalc.navigation.BottomNavigationBar
import it.lcavagnari.pdm.dermcalc.navigation.HomeRoute
import it.lcavagnari.pdm.dermcalc.navigation.navItems
import it.lcavagnari.pdm.dermcalc.ui.portrait.component.TopMenu
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.OnboardingScreen
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.onboardingScreens

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainPortraitActivity(
    modifier: Modifier = Modifier,
    onboardingModel: OnboardingModel,
    startingDestination: AppRoute = HomeRoute,
    onToggleTheme: () -> Unit = {}
) {
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
                onFinish = { onboardingModel.finishOnboarding() },
                onToggleTheme = onToggleTheme
            )
        }


    } else Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Log.d("MainActivity", "BottomNavigationBar")
            TopMenu(navController, onToggleTheme = onToggleTheme)
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                appItems = navItems
            )
        }

    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = modifier.padding(innerPadding),
            onboardingModel = onboardingModel,
            startDestination = startingDestination,
        )
    }
}


// Preview
private val vm = OnboardingModel()

@SuppressLint("NewApi")
@Preview(showBackground = true)
@Composable
fun MainPortraitActivityPreview() {
    vm.finishOnboarding()
    MainPortraitActivity(onboardingModel = vm)
}
