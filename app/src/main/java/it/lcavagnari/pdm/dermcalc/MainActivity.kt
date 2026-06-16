package it.lcavagnari.pdm.dermcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import it.lcavagnari.pdm.dermcalc.data.AppDatabase
import it.lcavagnari.pdm.dermcalc.data.AppSettingsEntity
import it.lcavagnari.pdm.dermcalc.data.DermCalcViewModelFactory
import it.lcavagnari.pdm.dermcalc.navigation.AppRoute
import it.lcavagnari.pdm.dermcalc.models.BodyScanModel
import it.lcavagnari.pdm.dermcalc.navigation.HomeRoute
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.navigation.ProfileRoute
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.navigation.ToolsRoute
import it.lcavagnari.pdm.dermcalc.navigation.AppNavHost
import it.lcavagnari.pdm.dermcalc.ui.component.NavigationBar
import it.lcavagnari.pdm.dermcalc.ui.component.TopMenu
import it.lcavagnari.pdm.dermcalc.ui.screens.LoadingScreen
import it.lcavagnari.pdm.dermcalc.ui.screens.OnboardingScreen
import it.lcavagnari.pdm.dermcalc.ui.screens.onboardingScreens
import it.lcavagnari.pdm.dermcalc.ui.theme.DermCalcTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalDarkTheme
import kotlinx.coroutines.launch

/**
 * Root activity. Detects orientation and delegates rendering to either
 * [MainActivity].
 */
class MainActivity : ComponentActivity() {
    /**
     * Initializes edge-to-edge UI, wires the theme toggle, and sets the root Compose content.
     *
     * @param savedInstanceState prior state bundle, or null on first launch.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val database = remember { AppDatabase.getInstance(this@MainActivity) }
            val factory = remember { DermCalcViewModelFactory(database, this@MainActivity) }

            val onboardingModel = ViewModelProvider(this, factory)[OnboardingModel::class.java]
            val toolsModel = ViewModelProvider(this, factory)[ToolsModel::class.java]
            val quoteModel = ViewModelProvider(this, factory)[QuoteModel::class.java]
            val bodyScanModel = ViewModelProvider(this, factory)[BodyScanModel::class.java]
            // Seed the initial quote; updateQuote() is not called on init.
            LaunchedEffect(Unit) { quoteModel.updateQuote() }

            val scope = rememberCoroutineScope()
            val configuration = LocalConfiguration.current
            val systemDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemDark) }
            var currentSettings by remember { mutableStateOf<AppSettingsEntity?>(null) }

            // Persist theme changes
            LaunchedEffect(Unit) {
                database.appSettingsDao().getSettings().collect { settings ->
                    currentSettings = settings
                    if (settings != null) {
                        isDarkTheme = settings.isDarkTheme
                    }
                }
            }

            DermCalcTheme(darkTheme = isDarkTheme, onToggleDarkTheme = {
                isDarkTheme = !isDarkTheme
                scope.launch {
                    currentSettings?.let { settings ->
                        database.appSettingsDao()
                            .upsert(settings.copy(isDarkTheme = !settings.isDarkTheme))
                    }
                }
            }) {
                AppMain(
                    Modifier,
                    onboardingModel,
                    bodyScanModel,
                    toolsModel,
                    quoteModel,
                    onToggleTheme = { isDarkTheme = !isDarkTheme })
            }
        }
    }
}

/**
 * Root portrait composable. Switches between the onboarding flow and the main app shell
 * based on [it.lcavagnari.pdm.dermcalc.models.OnboardingModel.hasSeenOnboarding].
 *
 * Two rendering paths:
 * - `!hasSeenOnboarding` → bare [androidx.compose.material3.Scaffold] (no top/bottom bars) containing [OnboardingScreen].
 * - `hasSeenOnboarding`  → background image behind a transparent [androidx.compose.material3.Scaffold] with [TopMenu] top bar,
 *   [NavigationBar] bottom bar, and [AppNavHost] content.
 *
 * @param modifier modifier applied to each scaffold's content slot.
 * @param onboardingModel view model providing onboarding state and field data.
 * @param toolsModel view model providing the list of stored [it.lcavagnari.pdm.dermcalc.models.ToolResult] entries.
 * @param quoteModel view model providing the currently displayed quote.
 * @param startingDestination initial navigation destination shown after onboarding completes.
 * @param onToggleTheme callback threaded through to [TopMenu] and [OnboardingScreen].
 */
@Composable
fun AppMain(
    modifier: Modifier = Modifier,
    onboardingModel: OnboardingModel,
    bodyScanModel: BodyScanModel,
    toolsModel: ToolsModel,
    quoteModel: QuoteModel,
    startingDestination: AppRoute = HomeRoute,
    onToggleTheme: () -> Unit = {}
) {
    val isOnboardingLoading by onboardingModel.isOnboardingLoading.collectAsState()
    val hasSeenOnboarding by onboardingModel.hasSeenOnboarding.collectAsState()
    val navController = rememberNavController()
    val pagerState = rememberPagerState(pageCount = { onboardingScreens.size })

    when {
        isOnboardingLoading -> LoadingScreen()

        !hasSeenOnboarding -> {
            OnboardingScreen(
                modifier = Modifier.fillMaxSize(),
                pagerState = pagerState,
                onboardingModel = onboardingModel,
                onFinish = { onboardingModel.finishOnboarding() },
                onToggleTheme = onToggleTheme
            )
        }

        else -> {
            Box(Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(if (LocalDarkTheme.current) R.drawable.bg_dark else R.drawable.bg),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    topBar = { TopMenu(navController, onToggleTheme = onToggleTheme) },
                    bottomBar = {
                        NavigationBar(
                            navController = navController,
                            appItems = listOf(HomeRoute, ToolsRoute, ProfileRoute)
                        )
                    }
                ) { innerPadding ->
                    AppNavHost(
                        modifier = modifier.padding(innerPadding),
                        startDestination = startingDestination,
                        navController = navController,
                        onboardingModel = onboardingModel,
                        bodyScanModel = bodyScanModel,
                        toolsModel = toolsModel,
                        quoteModel = quoteModel
                    )
                }
            }
        }
    }
}