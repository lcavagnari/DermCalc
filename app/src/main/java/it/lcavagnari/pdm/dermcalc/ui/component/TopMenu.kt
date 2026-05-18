package it.lcavagnari.pdm.dermcalc.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.navigation.HomeRoute
import it.lcavagnari.pdm.dermcalc.navigation.ProfileRoute
import it.lcavagnari.pdm.dermcalc.navigation.ToolsRoute
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import it.lcavagnari.pdm.dermcalc.ui.theme.DeterminationMono
import it.lcavagnari.pdm.dermcalc.ui.theme.Soul
import it.lcavagnari.pdm.dermcalc.ui.theme.soulForRoute


/**
 * Top app bar that displays the current destination title, subtitle, and icon,
 * with theme-toggle and debug action buttons in the trailing tray.
 *
 * @param navController - controller used to observe the current back-stack destination.
 * @param onToggleTheme - callback invoked when the user taps the theme-toggle button.
 * @param onDebugClick - callback invoked when the user taps the debug button.
 */
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

    // Per the theme guide (section 03 + 05):
    //   HOME  → SOUL Determination (red #E04848, the brand mark / launcher icon / heart)
    //   TOOLS → palette Determination "at rest" (gold primary; no calculator owns this screen)
    //   PROFILE → SOUL Kindness (green, self-care)
    // Each soul claims its own room — never share chrome between screens.
    val soulColor = when (currentDestination?.route) {
        HomeRoute.route -> Soul.Determination.color
        ToolsRoute.route -> MaterialTheme.colorScheme.primary
        ProfileRoute.route -> Soul.Kindness.color
        else -> soulForRoute(currentDestination?.route).color
    }
    // Use luminance to pick legible text: bright soul backgrounds (gold, green, cyan, orange)
    // need black; dark backgrounds (blue, purple, red, dark amber) take white.
    val onSoulColor = if (soulColor.luminance() > 0.18f) Color.Black else Color.White

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
                // Soul color fills behind the system status bar; content sits below it
                // so icons/text/buttons stay tappable and readable.
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                modifier           = Modifier.size(28.dp),
                painter            = painterResource(icon),
                contentDescription = null,
                tint               = onSoulColor
            )
            Column(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Determination Mono — the theme's actual title font. Pixel aesthetic
                // with proper glyph spacing, readable at chrome sizes.
                Text(
                    text       = stringResource(title),
                    fontFamily = DeterminationMono,
                    fontSize   = 27.sp,
                    fontWeight = FontWeight.Normal,
                    color      = onSoulColor,
                    maxLines   = 1,
                    softWrap   = false,
                )
                subtitle?.let {
                    Text(
                        text  = stringResource(it),
                        style = MaterialTheme.typography.labelMedium,
                        color = onSoulColor.copy(alpha = 0.85f),
                        fontSize = 16.sp
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
