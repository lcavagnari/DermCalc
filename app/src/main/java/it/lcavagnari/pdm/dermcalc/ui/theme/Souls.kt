/*
 * Copyright (C) 2026 Luca Cavagnari
 *
 * This file is part of DermCalc, final project for the Mobile Device Programming course of Univerità Degli Studi Dell'Insubria.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 */
package it.lcavagnari.pdm.dermcalc.ui.theme

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.navigation.HomeRoute
import it.lcavagnari.pdm.dermcalc.navigation.ProfileRoute
import it.lcavagnari.pdm.dermcalc.models.Severity
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
    Justice(SoulJustice);
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

/** Maps this [Severity] to its localized label string resource id. */
@get:StringRes
val Severity.labelRes: Int
    get() = when (this) {
        Severity.NONE -> R.string.severity_normal
        Severity.MILD -> R.string.severity_normal
        Severity.MODERATE -> R.string.severity_moderate
        Severity.SEVERE -> R.string.severity_severe
    }

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
        Severity.NONE     -> if (dark) SeverityMildDark     else SeverityMildLight
        Severity.MILD     -> if (dark) SeverityMildDark     else SeverityMildLight
        Severity.MODERATE -> if (dark) SeverityModerateDark else SeverityModerateLight
        Severity.SEVERE   -> if (dark) SeveritySevereDark   else SeveritySevereLight
    }
}


