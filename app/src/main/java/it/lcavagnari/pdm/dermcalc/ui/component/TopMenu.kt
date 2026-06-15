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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.BMIToolRoute
import it.lcavagnari.pdm.dermcalc.models.BSAToolRoute
import it.lcavagnari.pdm.dermcalc.models.EASIToolRoute
import it.lcavagnari.pdm.dermcalc.models.HomeRoute
import it.lcavagnari.pdm.dermcalc.models.PASIToolRoute
import it.lcavagnari.pdm.dermcalc.models.ProfileRoute
import it.lcavagnari.pdm.dermcalc.models.ToolsRoute
import it.lcavagnari.pdm.dermcalc.ui.component.input.ButtonsTray
import it.lcavagnari.pdm.dermcalc.ui.portrait.DermCalcPreview
import it.lcavagnari.pdm.dermcalc.ui.preview.previewBmiResults
import it.lcavagnari.pdm.dermcalc.ui.theme.DermCalcTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.DeterminationMono
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalBarAlpha
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulBravery
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulDetermination
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulIntegrity
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulJustice
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulKindness
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulPatience
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulPerseverance
import it.lcavagnari.pdm.dermcalc.ui.theme.soulForRoute

@Preview(showBackground = true) @Composable private fun TopMenuRegularPreview() {
    DermCalcTheme { TopMenu(rememberNavController())
    }
}
@Preview(showBackground = true) @Composable private fun TopMenuRegularDarkPreview() {
    DermCalcTheme(darkTheme = true) { TopMenu(rememberNavController()) }
}
@Preview(showBackground = true) @Composable private fun HomeScreenFullPreview() {
    DermCalcPreview(screen = HomeRoute, setupTm = previewBmiResults)
}
@Preview(showBackground = true) @Composable private fun HomeScreenFullDarkPreview() {
    DermCalcPreview(darkTheme = true, screen = HomeRoute, setupTm = previewBmiResults)
}

/** Returns the title string resource id for [route]. */
private fun title(route: String): Int {
    return when (route) {
        ToolsRoute.route    -> R.string.nav_tools
        ProfileRoute.route  -> R.string.nav_profile
        BMIToolRoute.route  -> R.string.tools_bmi
        BSAToolRoute.route  -> R.string.tools_bsa
        PASIToolRoute.route -> R.string.tools_pasi
        EASIToolRoute.route -> R.string.tools_easi
        else                -> R.string.app_name
    }
}
/** Returns the optional subtitle string resource id for [route]. */
private fun subtitle(route: String?): Int? {
    return when (route) {
        HomeRoute.route -> R.string.nav_home
        ToolsRoute.route -> R.string.nav_tools_subtitle
        ProfileRoute.route -> R.string.nav_profile_subtitle
        BMIToolRoute.route -> R.string.tools_bmi_description
        BSAToolRoute.route -> R.string.tools_bsa_description
        PASIToolRoute.route -> R.string.tools_pasi_description
        EASIToolRoute.route -> R.string.tools_easi_description
        else -> null
    }
}
/** Returns the leading icon drawable resource id for [route]. */
private fun icon(route: String?): Int {
    return when (route) {
        ToolsRoute.route -> R.drawable.ic_tools_calculator
        ProfileRoute.route -> R.drawable.ic_profile_button
        BMIToolRoute.route, BSAToolRoute.route, PASIToolRoute.route, EASIToolRoute.route -> R.drawable.ic_arrow_back
        else -> R.drawable.ic_dermatology
    }
}
/** Returns the soul accent [Color] for [route]. */
private fun soulColor(route: String?): Color {
    return when (route) {
        HomeRoute.route -> SoulDetermination
        ToolsRoute.route -> SoulJustice
        ProfileRoute.route -> SoulKindness
        BMIToolRoute.route -> SoulPatience
        BSAToolRoute.route -> SoulBravery
        PASIToolRoute.route -> SoulIntegrity
        EASIToolRoute.route -> SoulPerseverance
        else -> soulForRoute(route).color
    }
}



/**
 * Top app bar. A full-width [androidx.compose.material3.Card] whose chrome color tracks the active soul.
 *
 * Layout (horizontal [Row]):
 * - Leading icon — ECG logo on Home; destination icon on other screens. On calculator screens
 *   the icon is clickable and pops the back stack to [it.lcavagnari.pdm.dermcalc.navigation.ToolsRoute].
 * - Title column — destination title (27sp) and optional subtitle (18sp), both in the soul color.
 * - Trailing [it.lcavagnari.pdm.dermcalc.ui.component.input.ButtonsTray] — language, theme-toggle; debug button omitted at this call site.
 *
 * Soul-color assignment per destination:
 * - Home → Determination (red)   · Tools  → Justice (gold)      · Profile → Kindness (green)
 * - BMI  → Patience (cyan)       · BSA    → Bravery (orange)
 * - PASI → Integrity (blue)      · EASI   → Perseverance (purple)
 *
 * Text and icon tint are chosen via luminance: bright soul backgrounds get black, dark ones get white.
 *
 * @param navController controller used to observe the current back-stack destination.
 * @param onToggleTheme callback forwarded to [it.lcavagnari.pdm.dermcalc.ui.component.input.ButtonsTray].
 */
@Composable
fun TopMenu(navController: NavController, onToggleTheme: () -> Unit = {}) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    val title: Int = title(currentDestination?.route?: "")
    val subtitle: Int? = subtitle(currentDestination?.route)
    val icon = icon(currentDestination?.route)
    // Each soul claims its own room — never share chrome between screens.
    val soulColor = soulColor(currentDestination?.route)

    val localAlpha = LocalBarAlpha.current
    val contentColor = MaterialTheme.colorScheme.inverseSurface

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraSmall,
        colors = CardDefaults.cardColors(
            containerColor = soulColor.copy(alpha = localAlpha),
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(2.dp,
            soulColor.copy(alpha = localAlpha)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Soul color fills behind the system status bar; content sits below it
                // so icons/text/buttons stay tappable and readable.
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(35.dp)
                    .clickable {
                        when (currentDestination?.route) {
                            BMIToolRoute.route, BSAToolRoute.route,
                            PASIToolRoute.route, EASIToolRoute.route ->
                                navController.popBackStack(ToolsRoute.route, false)

                            else -> {}
                        }
                    },
                painter = painterResource(icon),
                contentDescription = null,
                tint = contentColor
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Text(
                    text = stringResource(title).uppercase(),
                    fontFamily = DeterminationMono,
                    fontSize = 25.sp,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    softWrap = false,
                )
                subtitle?.let {
                    Text(
                        text = stringResource(it),
                        style = MaterialTheme.typography.labelMedium,
                        color = contentColor.copy(alpha = 0.85f),
                        fontSize = 18.sp
                    )
                }
            }
            ButtonsTray(iconTint = contentColor, onToggleTheme = onToggleTheme) {}
        }
    }
}
