package it.lcavagnari.pdm.dermcalc.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute {
    val name: String?
    val route: String
    val icon: ImageVector
}

@Serializable
data object HomeRoute : AppRoute {
    override val name: String
        get() = "Home"
    override val route: String
        get() = "home"
    override val icon: ImageVector
        get() = Icons.Default.Home
}

@Serializable
data object ToolsRoute : AppRoute {
    override val name: String
        get() = "Tools"
    override val route: String
        get() = "tools"
    override val icon: ImageVector
        get() = Icons.Default.Build
}

@Serializable
data object ProfileRoute : AppRoute {
    override val name: String
        get() = "profile"
    override val route: String
        get() = "profile"
    override val icon: ImageVector
        get() = Icons.Default.AccountCircle
}

val navItems = listOf(HomeRoute, ToolsRoute, ProfileRoute)