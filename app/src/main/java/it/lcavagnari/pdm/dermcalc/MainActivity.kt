package it.lcavagnari.pdm.dermcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.ViewModelProvider
import it.lcavagnari.pdm.dermcalc.data.AppDatabase
import it.lcavagnari.pdm.dermcalc.data.AppSettingsEntity
import it.lcavagnari.pdm.dermcalc.data.DermCalcViewModelFactory
import it.lcavagnari.pdm.dermcalc.models.BodyScanModel
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.ui.landscape.MainLandscapeActivity
import it.lcavagnari.pdm.dermcalc.ui.portrait.MainPortraitActivity
import it.lcavagnari.pdm.dermcalc.ui.theme.DermCalcTheme
import kotlinx.coroutines.launch

/**
 * Root activity. Detects orientation and delegates rendering to either
 * [MainPortraitActivity] or [MainLandscapeActivity].
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
                        database.appSettingsDao().upsert(settings.copy(isDarkTheme = !settings.isDarkTheme))
                    }
                }
            }) {
                MainPortraitActivity(
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


