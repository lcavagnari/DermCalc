package it.lcavagnari.pdm.dermcalc.ui.component

import android.app.Application
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.navigation.AppRoute
import it.lcavagnari.pdm.dermcalc.navigation.HomeRoute
import it.lcavagnari.pdm.dermcalc.navigation.ProfileRoute
import it.lcavagnari.pdm.dermcalc.navigation.ToolsRoute
import it.lcavagnari.pdm.dermcalc.ui.portrait.MainPortraitActivity
import it.lcavagnari.pdm.dermcalc.ui.theme.DermCalcTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalBarAlpha
import it.lcavagnari.pdm.dermcalc.ui.theme.Soul
import it.lcavagnari.pdm.dermcalc.ui.theme.onSoul
import it.lcavagnari.pdm.dermcalc.ui.theme.onSoulContainer
import it.lcavagnari.pdm.dermcalc.ui.theme.soulForRoute

/**
 * Renders bottom tabs and preserves per-destination back stack state.
 *
 * The selected item color follows the active destination: Home uses Determination, Tools uses the
 * Material primary color, Profile uses Kindness, and other routes fall back to [soulForRoute].
 * Tapping a different item navigates with single-top and state restoration enabled.
 *
 * @param navController controller used for tab navigation actions.
 * @param appItems routes rendered as bottom navigation entries.
 */
@Composable
fun NavigationBar(navController: NavController, appItems: List<AppRoute>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination


    val localAlpha = LocalBarAlpha.current
    val soulColor = when (currentDestination?.route) {
        HomeRoute.route -> Soul.Determination.color
        ToolsRoute.route -> MaterialTheme.colorScheme.primary
        ProfileRoute.route -> Soul.Kindness.color
        else -> soulForRoute(currentDestination?.route).color
    }

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .semantics { testTag = "bottom_nav_bar" },
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = localAlpha),
        tonalElevation = 6.dp,
        windowInsets = WindowInsets(bottom = 10.dp),
    ) {


        appItems.forEach { item ->
            // Using an explicit when block with hasRoute<T>() ensures the compiler uses
            // the correct type-safe extension and avoids "restricted API" errors.
            val isSelected = currentDestination
                ?.hierarchy?.any {
                    it.hasRoute(item::class)
                } == true

            NavigationBarItem(
                icon = {
                    if (item.iconRes != null) {
                        Icon(
                            painter = painterResource(id = item.iconRes!!),
                            contentDescription = item.route,
                            modifier = Modifier.size(30.dp),
                        )
                    } else {
                        item.icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = item.route,
                                modifier = Modifier.size(28.dp),
                            )
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = soulColor,
                    selectedTextColor = soulColor,
                    indicatorColor = soulColor.copy(alpha = 0.22f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                selected = isSelected,
                onClick = {
                    if (currentDestination?.route != item.route) navController.navigate(item) {
                        // Avoid multiple copies of the same destination when reselecting the same item
                        launchSingleTop = true

                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPortraitActivityPreview() {
    val app = object : Application() { init { attachBaseContext(LocalContext.current) } }
    val vm = remember { OnboardingModel(app) }.also { it.finishOnboarding() }
    val qm = remember { QuoteModel(app) }.also { it.updateQuote() }
    val tm = remember { ToolsModel(app) }

    DermCalcTheme {
        MainPortraitActivity(quoteModel = qm, onboardingModel = vm, toolsModel = tm)
    }
}

@Preview(showBackground = true)
@Composable
fun MainPortraitActivityPreview1() {
    NavigationBar(
        navController = rememberNavController(),
        appItems = listOf(HomeRoute, ToolsRoute, ProfileRoute)
    )
}
