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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.navigation.HomeRoute
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.Quote
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.data.TextInput
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.ui.component.BorderSide
import it.lcavagnari.pdm.dermcalc.ui.component.BorderedCard
import it.lcavagnari.pdm.dermcalc.ui.component.HistoryCard
import it.lcavagnari.pdm.dermcalc.ui.component.HistoryResultRow
import it.lcavagnari.pdm.dermcalc.ui.component.input.ButtonsTray
import it.lcavagnari.pdm.dermcalc.ui.component.input.LabelPosition
import it.lcavagnari.pdm.dermcalc.ui.preview.DermCalcPreview
import it.lcavagnari.pdm.dermcalc.ui.preview.previewBmiResults
import it.lcavagnari.pdm.dermcalc.ui.theme.DeterminationMono
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulJustice
import it.lcavagnari.pdm.dermcalc.utils.today
import kotlinx.coroutines.delay
import kotlinx.datetime.number

@Preview(showBackground = true) @Composable private fun HomeScreenFullPreview() {
    DermCalcPreview(screen = HomeRoute, setupTm = previewBmiResults)
}
@Preview(showBackground = true) @Composable private fun HomeScreenFullDarkPreview() {
    DermCalcPreview(darkTheme = true, screen = HomeRoute, setupTm = previewBmiResults)
}

/**
 * Home screen. Centered column of three stacked cards at 90% screen width.
 */
@Composable
fun HomeScreen(
    quoteModel: QuoteModel,
    onboardingModel: OnboardingModel,
    toolsModel: ToolsModel
) {
    val fields by onboardingModel.fields.collectAsState()
    val fullNameField = fields.firstOrNull() as? TextInput
    val welcomeMessage = if (fullNameField != null) {
        stringResource(R.string.welcome) + ", " + fullNameField.value.split(' ')[0]
    } else stringResource(R.string.welcome)


    val todayDate = today().date
    val configuration = LocalConfiguration.current
    val dateText = remember(todayDate, configuration) {
        val javaDate = java.time.LocalDate.of(todayDate.year, todayDate.month.number, todayDate.dayOfMonth)
        javaDate.format(java.time.format.DateTimeFormatter.ofPattern("EEEE dd MMMM", configuration.locales[0]))
    }

    var showHistoryOverlay by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.9f),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = welcomeMessage.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start
                    )
                }
            }

            QuoteCard(modifier = Modifier.fillMaxWidth(0.9f), quoteModel = quoteModel)

            HistoryCard(
                modifier = Modifier.fillMaxWidth(0.9f),
                toolsModel = toolsModel,
                onShowAll = { showHistoryOverlay = true }
            )
        }

        HistoryOverlay(
            visible = showHistoryOverlay,
            toolsModel = toolsModel,
            onClose = { showHistoryOverlay = false }
        )
    }
}

/**
     * Full-screen overlay with a bottom sheet listing all ToolResults with delete capability and a clear-all action.
     */
    @Composable
    fun HistoryOverlay(
        visible: Boolean = false,
        toolsModel: ToolsModel,
        onClose: () -> Unit
    ) {
    val rawResults by toolsModel.toolsResult.collectAsState()
    val results = remember(rawResults) {
        rawResults.sortedByDescending { it.timestamp }
    }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(visible) {
        if (visible) {
            isLoading = true
            delay(500)
            isLoading = false
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(onClick = onClose, indication = null, interactionSource = remember { MutableInteractionSource() })
            )
            // Sheet
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .align(Alignment.BottomCenter)
                    .animateEnterExit(
                        enter = slideInVertically { it },
                        exit = slideOutVertically { it }
                    ),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = stringResource(R.string.nav_history).uppercase(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontFamily = DeterminationMono,
                                color = SoulJustice,
                                letterSpacing = 2.sp
                            )
                            AnimatedContent(
                                targetState = results.size,
                                transitionSpec = { fadeIn() togetherWith fadeOut() }
                            ) { count ->
                                Text(
                                    text = if (count == 0) stringResource(R.string.history_empty_short)
                                    else pluralStringResource(R.plurals.history_record_count, count, count),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        ButtonsTray(
                            iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                            onClose = onClose,
                            onReset = if (results.isNotEmpty()) {
                                { toolsModel.clearAllResults() }
                            } else {
                                null
                            },
                            resetLabel = stringResource(R.string.btn_clear),
                            resetSoulColor = MaterialTheme.colorScheme.error,
                            resetLabelPosition = LabelPosition.Below,
                            resetDialogTitle = stringResource(R.string.clear_history_title),
                            resetDialogBody = stringResource(R.string.clear_history_body),
                            resetDialogConfirmLabel = stringResource(R.string.btn_clear),
                        )
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    // Content
                    when {
                        isLoading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = SoulJustice, strokeWidth = 2.dp)
                            }
                        }

                        results.isEmpty() -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.padding(horizontal = 48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.History,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                                    )
                                    Text(
                                        text = stringResource(R.string.history_empty),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        else -> {
                            var now by remember { mutableStateOf(today()) }
                            LaunchedEffect(Unit) {
                                while (true) {
                                    delay(30_000)
                                    now = today()
                                }
                            }
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(results, key = { it.id }) { result ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateItem(),
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        tonalElevation = 0.dp
                                    ) {
                                        HistoryResultRow(
                                            modifier = Modifier,
                                            result = result,
                                            now = now,
                                            onDelete = {
                                                toolsModel.deleteResult(result)
                                                if (results.isEmpty()) onClose()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card displaying the current dermatology quote from [quoteModel].
 */
@Composable
fun QuoteCard(modifier: Modifier = Modifier, quoteModel: QuoteModel) {
    val quote: Quote = quoteModel.homeQuote.collectAsState().value

    BorderedCard(
        modifier = modifier,
        borderSide = BorderSide.Left,
        borderColor = MaterialTheme.colorScheme.tertiary,
        borderStrokeWidth = 3.dp,
        cornerRadius = 12.dp,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        onClick = { quoteModel.updateQuote() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 15.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = quote.value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.End)
                    .padding(top = 2.dp, end = 5.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                if (quote.author.isNullOrBlank()) Text(
                    text = stringResource(R.string.quote_tip),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp,
                    maxLines = 2,
                ) else Text(
                    text = quote.author,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End,
                    fontSize = 17.sp
                )
            }
        }
    }
}
