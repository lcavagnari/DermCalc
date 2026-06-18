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
package it.lcavagnari.pdm.dermcalc.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalDarkTheme

/**
 * Full-screen loading placeholder displayed while [OnboardingModel] loads persisted state
 * from Room. Shows the app background, a spinning indicator at 1/3 screen height, and the
 * app icon centered.
 */
@Composable
fun LoadingScreen() {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(
                if (LocalDarkTheme.current) R.drawable.loading_dark
                else R.drawable.loading_light
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxHeight(0.33f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp
                )
            }
        }
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_round),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center)
                .size(120.dp)
        )
    }
}