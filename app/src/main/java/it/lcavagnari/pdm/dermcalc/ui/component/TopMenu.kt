package it.lcavagnari.pdm.dermcalc.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.navigation.BMIToolRoute
import it.lcavagnari.pdm.dermcalc.navigation.BSAToolRoute
import it.lcavagnari.pdm.dermcalc.navigation.EASIToolRoute
import it.lcavagnari.pdm.dermcalc.navigation.HomeRoute
import it.lcavagnari.pdm.dermcalc.navigation.PASIToolRoute
import it.lcavagnari.pdm.dermcalc.navigation.ProfileRoute
import it.lcavagnari.pdm.dermcalc.navigation.ToolsRoute
import it.lcavagnari.pdm.dermcalc.ui.component.input.TopTrayButtons
import it.lcavagnari.pdm.dermcalc.ui.theme.DeterminationMono
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulBravery
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulDetermination
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulIntegrity
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulJustice
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulKindness
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulPatience
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulPerseverance
import it.lcavagnari.pdm.dermcalc.ui.theme.soulForRoute


/**
 * Top app bar. A full-width [androidx.compose.material3.Card] whose chrome color tracks the active soul.
 *
 * Layout (horizontal [Row]):
 * - Leading icon — ECG logo on Home; destination icon on other screens. On calculator screens
 *   the icon is clickable and pops the back stack to [it.lcavagnari.pdm.dermcalc.navigation.ToolsRoute].
 * - Title column — destination title (27sp) and optional subtitle (18sp), both in the soul color.
 * - Trailing [it.lcavagnari.pdm.dermcalc.ui.component.input.TopTrayButtons] — language, theme-toggle; debug button omitted at this call site.
 *
 * Soul-color assignment per destination:
 * - Home → Determination (red)   · Tools  → Justice (gold)      · Profile → Kindness (green)
 * - BMI  → Patience (cyan)       · BSA    → Bravery (orange)
 * - PASI → Integrity (blue)      · EASI   → Perseverance (purple)
 *
 * Text and icon tint are chosen via luminance: bright soul backgrounds get black, dark ones get white.
 *
 * @param navController controller used to observe the current back-stack destination.
 * @param onToggleTheme callback forwarded to [it.lcavagnari.pdm.dermcalc.ui.component.input.TopTrayButtons].
 */
@Composable
fun TopMenu(navController: NavController, onToggleTheme: () -> Unit = {}) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    val title: Int = when (currentDestination?.route) {
        ToolsRoute.route    -> R.string.nav_tools
        ProfileRoute.route  -> R.string.nav_profile
        BMIToolRoute.route  -> R.string.tools_bmi
        BSAToolRoute.route  -> R.string.tools_bsa
        PASIToolRoute.route -> R.string.tools_pasi
        EASIToolRoute.route -> R.string.tools_easi
        else -> R.string.app_name
    }

    val subtitle: Int? = when (currentDestination?.route) {
        HomeRoute.route -> R.string.nav_home_subtitle
        ToolsRoute.route -> R.string.nav_tools_subtitle
        ProfileRoute.route -> R.string.nav_profile_subtitle
        BMIToolRoute.route  -> R.string.tools_bmi_description
        BSAToolRoute.route  -> R.string.tools_bsa_description
        PASIToolRoute.route -> R.string.tools_pasi_description
        EASIToolRoute.route -> R.string.tools_easi_description
        else -> null
    }

    val icon = when (currentDestination?.route) {
        ToolsRoute.route -> R.drawable.ic_tools_calculator
        ProfileRoute.route -> R.drawable.ic_profile_button
        BMIToolRoute.route, BSAToolRoute.route, PASIToolRoute.route, EASIToolRoute.route -> R.drawable.ic_arrow_back
        else -> R.drawable.ic_ecg
    }

    // Per the theme guide (section 03 + 05):
    //   HOME  → SOUL Determination (red #E04848, the brand mark / launcher icon / heart)
    //   TOOLS → palette Determination "at rest" (gold primary; no calculator owns this screen)
    //   PROFILE → SOUL Kindness (green, self-care)
    // Each soul claims its own room — never share chrome between screens.
    val soulColor = when (currentDestination?.route) {
        HomeRoute.route     -> SoulDetermination
        ToolsRoute.route    -> SoulJustice
        ProfileRoute.route  -> SoulKindness
        BMIToolRoute.route  -> SoulPatience
        BSAToolRoute.route  -> SoulBravery
        PASIToolRoute.route -> SoulIntegrity
        EASIToolRoute.route -> SoulPerseverance
        else -> soulForRoute(currentDestination?.route).color
    }
    // Use luminance to pick legible text: bright soul backgrounds (gold, green, cyan, orange)
    // need black; dark backgrounds (blue, purple, red, dark amber) take white.
    val onSoulColor = if (soulColor.luminance() > 0.18f) Color.Black else Color.White

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = MaterialTheme.shapes.extraSmall,
        colors    = CardDefaults.cardColors(
            contentColor = soulColor,
            //containerColor = soulColor
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f))
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
                modifier           = Modifier
                    .size(35.dp)
                    .clickable {
                        when (currentDestination?.route) {
                            BMIToolRoute.route, BSAToolRoute.route,
                            PASIToolRoute.route, EASIToolRoute.route ->
                                navController.popBackStack(ToolsRoute.route, false)

                            else -> {}
                        }
                    },
                painter            = painterResource(icon),
                contentDescription = null,
                tint               = soulColor
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
                    color      = soulColor,
                    maxLines   = 1,
                    softWrap   = false,
                )
                subtitle?.let {
                    Text(
                        text  = stringResource(it),
                        style = MaterialTheme.typography.labelMedium,
                        color = soulColor.copy(alpha = 0.85f),
                        fontSize = 18.sp
                    )
                }
            }
            TopTrayButtons(
                iconTint = soulColor,
                onToggleTheme = onToggleTheme
            ) {}
        }
    }
}
