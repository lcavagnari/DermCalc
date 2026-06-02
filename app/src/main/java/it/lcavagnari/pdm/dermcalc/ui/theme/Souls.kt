package it.lcavagnari.pdm.dermcalc.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import it.lcavagnari.pdm.dermcalc.navigation.HomeRoute
import it.lcavagnari.pdm.dermcalc.navigation.ProfileRoute
import it.lcavagnari.pdm.dermcalc.navigation.ToolsRoute

/**
 * Undertale soul colors mapped to app destinations and tools.
 * Each soul owns a single screen — never share the same soul across two screens.
 *
 * @property color - the [Color] constant for this soul.
 */
enum class Soul(val color: Color) {
    Determination(SoulDetermination),
    Patience(SoulPatience),
    Bravery(SoulBravery),
    Integrity(SoulIntegrity),
    Perseverance(SoulPerseverance),
    Kindness(SoulKindness),
    Justice(SoulJustice)
}

/**
 * Returns the [Soul] assigned to [toolName], falling back to [Soul.Determination].
 *
 * @param toolName the [ToolResult.name] string (e.g. "BMI", "PASI").
 * @return The [Soul] for the given tool name.
 */
fun soulFor(toolName: String?): Soul = when (toolName) {
    "BMI"  -> Soul.Patience
    "BSA"  -> Soul.Bravery
    "PASI" -> Soul.Integrity
    "EASI" -> Soul.Perseverance
    else   -> Soul.Determination
}

/**
 * Returns the [Soul] assigned to [route], falling back to [Soul.Determination].
 *
 * @param route the route string from the nav back stack (e.g. [HomeRoute.route]).
 * @return The [Soul] for the given route.
 */
fun soulForRoute(route: String?): Soul = when (route) {
    ProfileRoute.route                -> Soul.Kindness
    HomeRoute.route, ToolsRoute.route -> Soul.Determination
    else                              -> Soul.Determination
}

/** Clinical severity tier used to color-code tool results throughout the app. */
enum class Severity { Mild, Moderate, Severe }

/**
 * Returns the theme-aware [Color] for [severity], switching between dark and light variants.
 *
 * @param severity the [Severity] tier to resolve.
 * @return The severity [Color] for the current theme.
 */
@Composable
fun severityColor(severity: Severity): Color {
    val dark = LocalDarkTheme.current
    return when (severity) {
        Severity.Mild     -> if (dark) SeverityMildDark     else SeverityMildLight
        Severity.Moderate -> if (dark) SeverityModerateDark else SeverityModerateLight
        Severity.Severe   -> if (dark) SeveritySevereDark   else SeveritySevereLight
    }
}
