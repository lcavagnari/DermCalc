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
package it.lcavagnari.pdm.dermcalc.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import it.lcavagnari.pdm.dermcalc.R
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Sealed interface for type-safe navigation destinations, carrying metadata for the bottom navigation bar.
 *
 * @property title - string resource id for the destination label.
 * @property route - unique route string registered with the nav graph.
 * @property icon - optional vector icon for the bottom nav item.
 * @property iconRes - optional drawable resource id for the bottom nav icon.
 */
@Serializable
sealed interface AppRoute {
    val title: Int? get() = null

    val icon: ImageVector? get() = null
    val iconRes: Int? get() = null

    val route: String
}


/** Home destination metadata for route graph and bottom navigation item. */
@Serializable
@SerialName("home")
data object HomeRoute : AppRoute {
    override val title: Int
        get() = R.string.nav_home
    override val route: String
        get() = "home"

    override val iconRes: Int
        get() = R.drawable.ic_home_button
}

/** Tools destination metadata representing calculators and utility workflows. */
@Serializable
@SerialName("tools")
data object ToolsRoute : AppRoute {
    override val title: Int
        get() = R.string.nav_tools
    override val route: String
        get() = "tools"
    override val iconRes: Int
        get() = R.drawable.ic_tools_calculator
}

/** Profile destination metadata used by navigation and tab rendering. */
@Serializable
@SerialName("profile")
data object ProfileRoute : AppRoute {
    override val title: Int
        get() = R.string.nav_profile
    override val route: String
        get() = "profile"
    override val iconRes: Int
        get() = R.drawable.ic_profile_button
}


/** BMI calculator destination metadata. */
@Serializable
@SerialName("bmitool")
data object BMIToolRoute: AppRoute {
    override val title: Int
        get() = R.string.tools_bmi
    override val route: String
        get() = "bmitool"
    override val iconRes: Int
        get() = R.drawable.ic_body_mass_index
}

/** BSA (Body Surface Area) calculator destination metadata. */
@Serializable
@SerialName("bsatool")
data object BSAToolRoute: AppRoute {
    override val title: Int
        get() = R.string.tools_bsa
    override val route: String
        get() = "bsatool"
    override val iconRes: Int
        get() = R.drawable.ic_body
}

/** PASI (Psoriasis Area and Severity Index) calculator destination metadata. */
@Serializable
@SerialName("pasitool")
data object PASIToolRoute: AppRoute {
    override val title: Int
        get() = R.string.tools_pasi
    override val route: String
        get() = "pasitool"
    override val iconRes: Int
        get() = R.drawable.ic_body_scan
}

/** EASI (Eczema Area and Severity Index) calculator destination metadata. */
@Serializable
@SerialName("easitool")
data object EASIToolRoute: AppRoute {
    override val title: Int
        get() = R.string.tools_easi
    override val route: String
        get() = "easitool"
    override val iconRes: Int
        get() = R.drawable.ic_allergies
}

