package it.lcavagnari.pdm.dermcalc.navigation

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import it.lcavagnari.pdm.dermcalc.R
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute {
    val name: String? get() = ""

    val nameRes: Int? get() = null
    val route: String
    val icon: ImageVector? get() = null

    val iconRes: Int? get() = null
}

@Serializable
data object HomeRoute : AppRoute {
    override val nameRes: Int
        get() = R.string.nav_home
    override val route: String
        get() = "home"
    override val iconRes: Int
        get() = R.drawable.ic_home_button
}

@Serializable
data object ToolsRoute : AppRoute {
    override val nameRes: Int
        get() = R.string.nav_tools
    override val route: String
        get() = "tools"
    override val icon: ImageVector
        get() = Icons.Default.Build
}

@Serializable
data object ProfileRoute : AppRoute {
    override val nameRes: Int
        get() = R.string.nav_profile
    override val route: String
        get() = "profile"
    override val iconRes: Int
        get() = R.drawable.ic_profile_button
}

val navItems = listOf(HomeRoute, ToolsRoute, ProfileRoute)