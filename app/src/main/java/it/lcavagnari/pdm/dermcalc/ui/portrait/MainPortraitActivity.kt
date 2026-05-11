package it.lcavagnari.pdm.dermcalc.ui.portrait

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.navigation.AppNavHost
import it.lcavagnari.pdm.dermcalc.navigation.BottomNavigationBar
import it.lcavagnari.pdm.dermcalc.navigation.navItems
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.OnboardingScreen
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.onboardingScreens
import it.lcavagnari.pdm.dermcalc.ui.theme.DermCalcTheme

/**
 * Hosts the root Compose scaffold and route graph.
 *
 * See app icon assets: app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml.
 */
class MainActivity : ComponentActivity() {
    /**
     * Initializes edge-to-edge UI and navigation-enabled application content.
     *
     * @param savedInstanceState Prior state bundle, or null on first launch.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val onboardingModel = ViewModelProvider(this)[OnboardingModel::class.java]
            Greeting(onboardingModel = onboardingModel)
        }
    }
}

@Composable
        /**
         * Simple preview text composable used as starter template content.
         *
         * @param name Name rendered inside greeting text.
         * @param modifier Modifier applied to the text node.
         * @return Unit.
         */
fun Greeting(modifier: Modifier = Modifier, onboardingModel: OnboardingModel) {
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

/**
 * Design-time preview showing scaffold, bottom navigation, and start destination.
 *
 * @return Unit.
 */
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Greeting(onboardingModel = OnboardingModel())
}
