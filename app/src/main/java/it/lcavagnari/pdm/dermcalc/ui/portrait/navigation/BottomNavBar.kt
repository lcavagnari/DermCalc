package it.lcavagnari.pdm.dermcalc.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Renders bottom tabs and preserves per-destination back stack state.
 *
 * @param navController - controller used for tab navigation actions.
 * @param appItems - routes rendered as bottom navigation entries.
 */
@Composable
fun BottomNavigationBar(navController: NavController, appItems: List<AppRoute>) {
    NavigationBar(
        containerColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

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
                            tint = Color.Unspecified
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
                    item.nameRes?.let {
                        Text(text = stringResource(id = it))
                    } ?: Text(text = item.name ?: "")
                },
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
