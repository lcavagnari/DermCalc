package it.lcavagnari.pdm.dermcalc.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.Severity
import it.lcavagnari.pdm.dermcalc.models.ToolResult
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.models.formattedScore
import it.lcavagnari.pdm.dermcalc.models.severity
import it.lcavagnari.pdm.dermcalc.ui.portrait.DermCalcPreview
import it.lcavagnari.pdm.dermcalc.ui.preview.previewBmiResults
import it.lcavagnari.pdm.dermcalc.utils.today
import kotlinx.datetime.LocalDateTime

/** Maximum number of results shown before a "Show all" row appears. **/
private const val MAX_HISTORY_VISIBLE = 5

@Preview(showBackground = true) @Composable private fun HistoryRegularPreview() {
    DermCalcPreview(setupTm = previewBmiResults) { _,_,tm,_ -> HistoryCard (toolsModel = tm, onShowAll = {}) }
}
@Preview(showBackground = true) @Composable private fun HistoryRegularDarkPreview() {
    DermCalcPreview(darkTheme = true,setupTm = previewBmiResults,) { _,_,tm,_ -> HistoryCard (toolsModel = tm, onShowAll = {}) }
}

/**
 * Card that displays the most recent [ToolResult] entries from [toolsModel].
 *
 * Shows up to [MAX_HISTORY_VISIBLE] rows in a snap-fling [LazyColumn]. When more results
 * exist than the visible limit, a "Show all" row is appended and [onShowAll] is invoked on tap.
 *
 * @param modifier modifier applied to the outer [Card].
 * @param toolsModel view model providing the [ToolResult] list via a [kotlinx.coroutines.flow.StateFlow].
 * @param onShowAll callback invoked when the user taps the "Show all" row.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryCard(
    modifier: Modifier = Modifier,
    toolsModel: ToolsModel,
    onShowAll: () -> Unit
) {
    val rawResults by toolsModel.toolsResult.collectAsState()
    val results = remember(rawResults) {
        rawResults.sortedByDescending { it.timestamp }
    }
    val now = remember { today() }
    val displayResults = results.take(MAX_HISTORY_VISIBLE)
    val hasMore = results.size > MAX_HISTORY_VISIBLE
    val scrollState = rememberLazyListState()

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = stringResource(R.string.home_history_title).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(bottom = 4.dp, top = 2.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 4.dp
            )

            if (results.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.history_empty),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    state = scrollState,
                    flingBehavior = rememberSnapFlingBehavior(scrollState)
                ) {
                    items(displayResults.size) { index ->
                        HistoryResultRow(result = displayResults[index], now = now)
                        if (index < displayResults.lastIndex || hasMore) HorizontalDivider(
                            modifier = Modifier.padding(vertical = 2.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                    if (hasMore) {
                        item { ShowAllRow(onClick = onShowAll) }
                    }
                }
            }
        }
    }
}


/**
 * A single result row showing the tool name, score, severity badge, and relative timestamp.
 */
@Composable
private fun HistoryResultRow(
    result: ToolResult,
    now: LocalDateTime
) {
    val daysAgo = (now.date.toEpochDays() - result.timestamp.date.toEpochDays()).toInt()
    val timeAgo = when {
        daysAgo == 0 -> stringResource(R.string.time_today)
        daysAgo == 1 -> pluralStringResource(R.plurals.history_days_ago, 1, 1)
        else -> pluralStringResource(R.plurals.history_days_ago, daysAgo, daysAgo)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = result.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = timeAgo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Text(
            text = result.formattedScore(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        if (result.severity() != Severity.NONE) {
            Text(
                text = result.severity().name,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}


/**
 * "Show all" row at the bottom of the history list.
 */
@Composable
private fun ShowAllRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.history_show_all),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}
