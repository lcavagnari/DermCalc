package it.lcavagnari.pdm.dermcalc.ui.portrait

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import it.lcavagnari.pdm.dermcalc.navigation.AppNavHost
import it.lcavagnari.pdm.dermcalc.navigation.BottomNavigationBar
import it.lcavagnari.pdm.dermcalc.navigation.navItems
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
            DermCalcTheme {
                val navController = rememberNavController()
                Scaffold(
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
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
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
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
/**
 * Design-time preview showing scaffold, bottom navigation, and start destination.
 *
 * @return Unit.
 */
fun GreetingPreview() {
    val navController = rememberNavController()
    Scaffold(
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
            modifier = Modifier.padding(innerPadding)
        )
    }
}
