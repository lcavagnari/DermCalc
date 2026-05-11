package it.lcavagnari.pdm.dermcalc

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.navigation.AppNavHost
import it.lcavagnari.pdm.dermcalc.navigation.BottomNavigationBar
import it.lcavagnari.pdm.dermcalc.navigation.navItems
import it.lcavagnari.pdm.dermcalc.ui.landscape.MainLandscapeActivity
import it.lcavagnari.pdm.dermcalc.ui.portrait.MainPortraitActivity

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
            val configuration = LocalConfiguration.current
            val onboardingModel = ViewModelProvider(this)[OnboardingModel::class.java]

            if (configuration.orientation != Configuration.ORIENTATION_LANDSCAPE)
                MainPortraitActivity(Modifier, onboardingModel)

            else MainLandscapeActivity(onboardingModel)
        }
    }
}