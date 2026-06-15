package it.lcavagnari.pdm.dermcalc.ui.portrait

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.rememberNavController
import it.lcavagnari.pdm.dermcalc.AppNavHost
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.BodyScanModel
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.data.UserProfileDao
import it.lcavagnari.pdm.dermcalc.data.AppSettingsDao
import it.lcavagnari.pdm.dermcalc.data.ToolResultDao
import it.lcavagnari.pdm.dermcalc.data.UserProfileEntity
import it.lcavagnari.pdm.dermcalc.data.AppSettingsEntity
import it.lcavagnari.pdm.dermcalc.data.ToolResultEntity
import kotlinx.coroutines.flow.MutableStateFlow
import it.lcavagnari.pdm.dermcalc.models.AppRoute
import it.lcavagnari.pdm.dermcalc.models.HomeRoute
import it.lcavagnari.pdm.dermcalc.models.ProfileRoute
import it.lcavagnari.pdm.dermcalc.models.ToolsRoute
import it.lcavagnari.pdm.dermcalc.ui.component.NavigationBar
import it.lcavagnari.pdm.dermcalc.ui.component.TopMenu
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.LoadingScreen
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.OnboardingScreen
import it.lcavagnari.pdm.dermcalc.ui.portrait.screens.onboardingScreens
import it.lcavagnari.pdm.dermcalc.ui.theme.DermCalcTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalDarkTheme

/**
 * Root portrait composable. Switches between the onboarding flow and the main app shell
 * based on [it.lcavagnari.pdm.dermcalc.models.OnboardingModel.hasSeenOnboarding].
 *
 * Two rendering paths:
 * - `!hasSeenOnboarding` → bare [androidx.compose.material3.Scaffold] (no top/bottom bars) containing [OnboardingScreen].
 * - `hasSeenOnboarding`  → background image behind a transparent [androidx.compose.material3.Scaffold] with [TopMenu] top bar,
 *   [NavigationBar] bottom bar, and [it.lcavagnari.pdm.dermcalc.AppNavHost] content.
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
                        NavigationBar(navController = navController,
                            appItems = listOf(HomeRoute, ToolsRoute, ProfileRoute)
                        )
                    }
                ) { innerPadding -> AppNavHost(
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


//  Preview wrapper functions
@Composable
fun DermCalcPreview(
    screen: AppRoute = HomeRoute,
    darkTheme: Boolean = false,
    setupOm: (OnboardingModel) -> Unit = { it.finishOnboarding(); it.updateName("Asriel ") },
    setupQm: (QuoteModel) -> Unit = { it.updateQuote() },
    setupTm: (ToolsModel) -> Unit = {},
    setupBm: (BodyScanModel) -> Unit = {}
) {
    DermCalcPreview(darkTheme,setupOm,setupQm,setupTm,setupBm) { vm, qm, tm, bm ->
        MainPortraitActivity(quoteModel = qm, onboardingModel = vm, toolsModel = tm, bodyScanModel = bm, startingDestination = screen)
    }
}



@Composable
fun DermCalcPreview(
    darkTheme: Boolean = false,
    setupOm: (OnboardingModel) -> Unit = { it.finishOnboarding() },
    setupQm: (QuoteModel) -> Unit = { it.updateQuote() },
    setupTm: (ToolsModel) -> Unit = {},
    setupBm: (BodyScanModel) -> Unit = {},
    content: @Composable (OnboardingModel, QuoteModel, ToolsModel, BodyScanModel) -> Unit
) {
    val context = LocalContext.current
    val app = remember { object : Application() { init { attachBaseContext(context) } } }
    val vm = remember {
        OnboardingModel(
            object : UserProfileDao {
                override suspend fun upsert(profile: UserProfileEntity) {}
                override fun getProfile() = MutableStateFlow<UserProfileEntity?>(null)
            },
            object : AppSettingsDao {
                override suspend fun upsert(settings: AppSettingsEntity) {}
                override fun getSettings() = MutableStateFlow<AppSettingsEntity?>(null)
            }
        )
    }.also { setupOm(it) }
    val qm = remember { QuoteModel(app) }.also { setupQm(it) }
    val tm = remember {
        ToolsModel(
            object : ToolResultDao {
                override suspend fun upsert(result: ToolResultEntity) {}
                override fun getAll() = MutableStateFlow<List<ToolResultEntity>>(emptyList())
                override suspend fun deleteById(id: Long) {}
                override suspend fun deleteAll() {}
            }
        )
    }.also { setupTm(it) }
    val bm = remember { BodyScanModel(app) }.also { setupBm(it) }

    DermCalcTheme(darkTheme = darkTheme, content = { content(vm,qm,tm,bm) })
}
