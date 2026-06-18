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
package it.lcavagnari.pdm.dermcalc.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.ui.screens.onboardingScreens
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalDarkTheme

/**
 * Horizontal pager that renders one [OnBoardItem] per onboarding page.
 */
@Composable
fun OnboardingPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    onboardingModel: OnboardingModel,
    userScrollEnabled: Boolean = true
) {
    val dark = LocalDarkTheme.current
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        userScrollEnabled = userScrollEnabled,
    ) { page ->
        val screen = onboardingScreens[page]
        Box(Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(if (dark) screen.backgroundDark else screen.backgroundLight),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(bottom = 90.dp)
            ) {
                OnBoardItem(screen, onboardingModel)
            }
        }
    }
}


