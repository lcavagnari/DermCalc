package it.lcavagnari.pdm.dermcalc.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import it.lcavagnari.pdm.dermcalc.navigation.HomeRoute
import it.lcavagnari.pdm.dermcalc.navigation.ProfileRoute
import it.lcavagnari.pdm.dermcalc.navigation.ToolsRoute

enum class Soul(val color: Color) {
    Determination(SoulDetermination),
    Patience(SoulPatience),
    Bravery(SoulBravery),
    Integrity(SoulIntegrity),
    Perseverance(SoulPerseverance),
    Kindness(SoulKindness),
    Justice(SoulJustice)
}

fun soulFor(toolName: String?): Soul = when (toolName) {
    "BMI"  -> Soul.Patience
    "BSA"  -> Soul.Bravery
    "PASI" -> Soul.Integrity
    "EASI" -> Soul.Perseverance
    else   -> Soul.Determination
}

fun soulForRoute(route: String?): Soul = when (route) {
    ProfileRoute.route                -> Soul.Kindness
    HomeRoute.route, ToolsRoute.route -> Soul.Determination
    else                              -> Soul.Determination
}

enum class Severity { Mild, Moderate, Severe }

@Composable
fun severityColor(severity: Severity): Color {
    val dark = LocalDarkTheme.current
    return when (severity) {
        Severity.Mild     -> if (dark) SeverityMildDark     else SeverityMildLight
        Severity.Moderate -> if (dark) SeverityModerateDark else SeverityModerateLight
        Severity.Severe   -> if (dark) SeveritySevereDark   else SeveritySevereLight
    }
}
