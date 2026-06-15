package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.Quote
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.models.TextInput
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.models.HomeRoute
import it.lcavagnari.pdm.dermcalc.ui.component.BorderSide
import it.lcavagnari.pdm.dermcalc.ui.component.BorderedCard
import it.lcavagnari.pdm.dermcalc.ui.component.HistoryCard
import it.lcavagnari.pdm.dermcalc.ui.component.HistoryResultRow
import it.lcavagnari.pdm.dermcalc.ui.component.input.ActionConfirmDialog
import it.lcavagnari.pdm.dermcalc.ui.portrait.DermCalcPreview
import it.lcavagnari.pdm.dermcalc.ui.preview.previewBmiResults
import it.lcavagnari.pdm.dermcalc.ui.theme.DeterminationMono
import it.lcavagnari.pdm.dermcalc.ui.theme.SoulJustice
import it.lcavagnari.pdm.dermcalc.utils.today
import kotlinx.coroutines.delay
import kotlinx.datetime.number
import java.text.SimpleDateFormat
import java.util.Calendar

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
    navController: NavHostController,
    quoteModel: QuoteModel,
    onboardingModel: OnboardingModel,
    toolsModel: ToolsModel
) {
    val fullNameField: TextInput = onboardingModel.fields.collectAsState().value[0] as TextInput
    val welcomeMessage =
        stringResource(R.string.welcome) + ", " + fullNameField.value.split(' ')[0]

    val todayDate = today().date
    val configuration = LocalConfiguration.current
    val dateText = remember(configuration) {
        SimpleDateFormat("EEEE dd MMMM", configuration.locales[0]).format(
            Calendar.getInstance().apply {
                set(todayDate.year, todayDate.month.number - 1, todayDate.day)
            }.time
        )
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

            QuoteCard(quoteModel = quoteModel)

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
    var showClearConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(visible) {
        if (visible) {
            isLoading = true
            delay(500)
            isLoading = false
        } else {
            showClearConfirm = false
        }
    }

    if (showClearConfirm) ActionConfirmDialog(
            title = stringResource(R.string.clear_history_title),
            body = stringResource(R.string.clear_history_body),
            confirmLabel = stringResource(R.string.btn_clear),
            onConfirm = { toolsModel.clearResult() },
            onDismiss = { showClearConfirm = false }
        )


    AnimatedVisibility(
        visible = visible,
        // Slides up from bottom instead of growing from top
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 10.dp).padding(top = 10.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.nav_history).uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = DeterminationMono,
                        color = SoulJustice
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { showClearConfirm = true }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_reset_button),
                                contentDescription = stringResource(R.string.btn_clear_all_description),
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(onClick = onClose) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.btn_close_description),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 2.dp
                )

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SoulJustice)
                    }
                } else if (results.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.history_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                } else {
                    var now by remember { mutableStateOf(today()) }
                    LaunchedEffect(Unit) {
                        while (true) {
                            delay(30_000)
                            now = today()
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(results, key = { it.id }) { result ->
                            HistoryResultRow(
                                modifier = Modifier.animateItem(),
                                result = result, now = now,
                                onDelete = {
                                    toolsModel.deleteResult(result)
                                    if (results.isEmpty()) onClose()
                                }
                            )

                            if (results.last() != result)
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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
        modifier = modifier.fillMaxWidth(0.9f),
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
