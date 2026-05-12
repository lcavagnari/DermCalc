package it.lcavagnari.pdm.dermcalc

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.ViewModelProvider
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.ui.landscape.MainLandscapeActivity
import it.lcavagnari.pdm.dermcalc.ui.portrait.MainPortraitActivity
import androidx.compose.runtime.CompositionLocalProvider
import it.lcavagnari.pdm.dermcalc.ui.theme.DermCalcTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalToggleDarkTheme

/**
 * Root activity. Detects orientation and delegates rendering to either
 * [MainPortraitActivity] or [MainLandscapeActivity].
 */
class MainActivity : ComponentActivity() {
    /**
     * Initializes edge-to-edge UI, wires the theme toggle, and sets the root Compose content.
     *
     * @param savedInstanceState - prior state bundle, or null on first launch.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val configuration = LocalConfiguration.current
            val onboardingModel = ViewModelProvider(this)[OnboardingModel::class.java]
            val systemDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemDark) }

            CompositionLocalProvider(LocalToggleDarkTheme provides { isDarkTheme = !isDarkTheme }) {
                DermCalcTheme(darkTheme = isDarkTheme) {
                    if (configuration.orientation != Configuration.ORIENTATION_LANDSCAPE)
                        MainPortraitActivity(Modifier, onboardingModel)
                    else MainLandscapeActivity(onboardingModel)
                }
            }
        }
    }
}