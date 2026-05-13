package it.lcavagnari.pdm.dermcalc.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.ui.graphics.vector.ImageVector
import it.lcavagnari.pdm.dermcalc.R
import kotlinx.serialization.Serializable

/**
 * Sealed interface for type-safe navigation destinations, carrying metadata for the bottom navigation bar.
 *
 * @property name - display name string (empty string default, prefer [nameRes]).
 * @property nameRes - string resource id for the destination label.
 * @property route - unique route string registered with the nav graph.
 * @property icon - optional vector icon for the bottom nav item.
 * @property iconRes - optional drawable resource id for the bottom nav icon.
 */
@Serializable
sealed interface AppRoute {
    val name: String? get() = ""

    val nameRes: Int? get() = null
    val route: String
    val icon: ImageVector? get() = null

    val iconRes: Int? get() = null
}

/** Home destination metadata for route graph and bottom navigation item. */
@Serializable
data object HomeRoute : AppRoute {
    override val nameRes: Int
        get() = R.string.nav_home
    override val route: String
        get() = "home"
    override val iconRes: Int
        get() = R.drawable.ic_home_button
}

/** Tools destination metadata representing calculators and utility workflows. */
@Serializable
data object ToolsRoute : AppRoute {
    override val nameRes: Int
        get() = R.string.nav_tools
    override val route: String
        get() = "tools"
    override val icon: ImageVector
        get() = Icons.Default.Build
}

/** Profile destination metadata used by navigation and tab rendering. */
@Serializable
data object ProfileRoute : AppRoute {
    override val nameRes: Int
        get() = R.string.nav_profile
    override val route: String
        get() = "profile"
    override val iconRes: Int
        get() = R.drawable.ic_profile_button
}

/** Static list consumed to render tabs in [BottomNavigationBar]. */
val navItems = listOf(HomeRoute, ToolsRoute, ProfileRoute)
