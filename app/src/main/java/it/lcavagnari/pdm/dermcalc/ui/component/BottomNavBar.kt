package it.lcavagnari.pdm.dermcalc.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import it.lcavagnari.pdm.dermcalc.navigation.AppRoute
import it.lcavagnari.pdm.dermcalc.navigation.HomeRoute
import it.lcavagnari.pdm.dermcalc.navigation.ProfileRoute
import it.lcavagnari.pdm.dermcalc.navigation.ToolsRoute
import it.lcavagnari.pdm.dermcalc.ui.theme.Soul
import it.lcavagnari.pdm.dermcalc.ui.theme.soulForRoute

/**
 * Renders bottom tabs and preserves per-destination back stack state.
 *
 * @param navController controller used for tab navigation actions.
 * @param appItems routes rendered as bottom navigation entries.
 */
@Composable
fun NavigationBar(navController: NavController, appItems: List<AppRoute>) {
    NavigationBar(
        modifier = Modifier.semantics { testTag = "bottom_nav_bar" },
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
        tonalElevation = 0.dp,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        // HOME = red SOUL Determination (brand mark / launcher icon / heart). TOOLS = gold
        // primary (palette Determination at rest; no calculator owns the screen).
        // PROFILE = green Kindness. Each soul claims its own room.
        val currentSoul = when (currentDestination?.route) {
            HomeRoute.route -> Soul.Determination.color
            ToolsRoute.route -> MaterialTheme.colorScheme.primary
            ProfileRoute.route -> Soul.Kindness.color
            else -> soulForRoute(currentDestination?.route).color
        }

        appItems.forEach { item ->
            // Using an explicit when block with hasRoute<T>() ensures the compiler uses
            // the correct type-safe extension and avoids "restricted API" errors.
            val isSelected = currentDestination
                ?.hierarchy
                ?.any {
                    it.hasRoute(item::class)
                } == true

            NavigationBarItem(
                icon = {
                    if (item.iconRes != null) {
                        Icon(
                            painter = painterResource(id = item.iconRes!!),
                            contentDescription = item.route,
                        )
                    } else {
                        item.icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = item.route
                            )
                        }
                    }
                },
                label = { Text(text = item.title?.let { stringResource(it) } ?: "") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = currentSoul,
                    selectedTextColor = currentSoul,
                    indicatorColor = currentSoul.copy(alpha = 0.22f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                selected = isSelected,
                onClick = {
                    navController.navigate(item) {
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
