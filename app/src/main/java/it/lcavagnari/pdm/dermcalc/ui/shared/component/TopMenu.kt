package it.lcavagnari.pdm.dermcalc.ui.shared.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.navigation.HomeRoute
import it.lcavagnari.pdm.dermcalc.navigation.ProfileRoute
import it.lcavagnari.pdm.dermcalc.navigation.ToolsRoute
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalDarkTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.Soul
import it.lcavagnari.pdm.dermcalc.ui.theme.soulForRoute

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun TopMenu(navController: NavController, onToggleTheme: () -> Unit = {}, onDebugClick: () -> Unit = {}) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    val title: Int = when (currentDestination?.route) {
        ToolsRoute.route -> R.string.nav_tools
        ProfileRoute.route -> R.string.nav_profile
        else -> R.string.app_name
    }

    val subtitle: Int? = when (currentDestination?.route) {
        HomeRoute.route -> R.string.nav_home_subtitle
        ToolsRoute.route -> R.string.nav_tools_subtitle
        ProfileRoute.route -> R.string.nav_profile_subtitle
        else -> null
    }

    val icon = when (currentDestination?.route) {
        ToolsRoute.route -> R.drawable.ic_tools_calculator
        ProfileRoute.route -> R.drawable.ic_profile_button
        else -> R.drawable.ic_ecg
    }

    val dark = LocalDarkTheme.current
    val soulColor = when (currentDestination?.route) {
        HomeRoute.route -> MaterialTheme.colorScheme.primary
        ToolsRoute.route -> Soul.Justice.color
        ProfileRoute.route -> Soul.Kindness.color
        else -> soulForRoute(currentDestination?.route).color
    }
    val onSoulColor = if (soulColor == MaterialTheme.colorScheme.primary && dark) androidx.compose.ui.graphics.Color.Black else androidx.compose.ui.graphics.Color.White

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = MaterialTheme.shapes.extraSmall,
        colors    = CardDefaults.cardColors(containerColor = soulColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border    = BorderStroke(1.dp, soulColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                modifier           = Modifier.size(32.dp),
                painter            = painterResource(icon),
                contentDescription = null,
                tint               = onSoulColor
            )
            Column(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text     = stringResource(title),
                    style    = MaterialTheme.typography.displaySmall,
                    color    = onSoulColor,
                    maxLines = 1,
                    softWrap = false
                )
                subtitle?.let {
                    Text(
                        text  = stringResource(it),
                        style = MaterialTheme.typography.labelMedium,
                        color = onSoulColor.copy(alpha = 0.78f)
                    )
                }
            }
            TopTrayButtons(
                iconTint      = onSoulColor,
                onToggleTheme = onToggleTheme,
                onDebugClick  = onDebugClick,
                showDebug     = true
            ) {}
        }
    }
}
