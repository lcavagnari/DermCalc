package it.lcavagnari.pdm.dermcalc.navigation

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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalDarkTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.Soul
import it.lcavagnari.pdm.dermcalc.ui.theme.soulForRoute

/**
 * Renders bottom tabs and preserves per-destination back stack state.
 *
 * @param navController - controller used for tab navigation actions.
 * @param appItems - routes rendered as bottom navigation entries.
 */
@Composable
fun NavigationBar(navController: NavController, appItems: List<AppRoute>) {
    NavigationBar(
        modifier = Modifier.semantics { testTag = "bottom_nav_bar" },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val dark = LocalDarkTheme.current
        val currentSoul = when (currentDestination?.route) {
            HomeRoute.route -> MaterialTheme.colorScheme.primary
            ToolsRoute.route -> Soul.Justice.color
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
                            contentDescription = item.name,
                                                    )
                    } else {
                        item.icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = item.name
                            )
                        }
                    }
                },
                label = {
                    item.title?.let {
                        Text(text = stringResource(id = it))
                    } ?: Text(text = item.name ?: "")
                },
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
                        // Pop up to the start destination of the graph to avoid building up a large stack
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }

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
