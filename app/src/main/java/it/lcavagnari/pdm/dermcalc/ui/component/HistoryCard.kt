package it.lcavagnari.pdm.dermcalc.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.BmiResult
import it.lcavagnari.pdm.dermcalc.models.BsaResult
import it.lcavagnari.pdm.dermcalc.models.EasiResult
import it.lcavagnari.pdm.dermcalc.models.PasiResult
import it.lcavagnari.pdm.dermcalc.models.ToolResult
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.ui.theme.LocalDarkTheme
import it.lcavagnari.pdm.dermcalc.ui.theme.Severity
import it.lcavagnari.pdm.dermcalc.ui.theme.Soul
import it.lcavagnari.pdm.dermcalc.ui.theme.severityColor
import it.lcavagnari.pdm.dermcalc.utils.today
import kotlinx.datetime.LocalDateTime

// Maximum number of results shown before a "Show all" row appears.
private const val MAX_HISTORY_VISIBLE = 4


/**
 * Maps a [ToolResult] to its clinical [Severity] tier using per-tool thresholds:
 * - PASI: < 10 Mild, < 20 Moderate, else Severe
 * - EASI: < 7 Mild, < 21 Moderate, else Severe
 * - BMI: < 18.5 Severe, < 25 Mild, < 30 Moderate, else Severe
 * - BSA: < 10 Mild, < 30 Moderate, else Severe
 */
private fun ToolResult.severity(): Severity = when (this) {
    is PasiResult -> when {
        score < 10.0 -> Severity.Mild
        score < 20.0 -> Severity.Moderate
        else -> Severity.Severe
    }
    is EasiResult -> when {
        score < 7.0 -> Severity.Mild
        score < 21.0 -> Severity.Moderate
        else -> Severity.Severe
    }
    is BmiResult -> when {
        score < 18.5 -> Severity.Severe
        score < 25.0 -> Severity.Mild
        score < 30.0 -> Severity.Moderate
        else -> Severity.Severe
    }
    is BsaResult -> when {
        score < 10.0 -> Severity.Mild
        score < 30.0 -> Severity.Moderate
        else -> Severity.Severe
    }
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
    val results = toolsModel.toolsResult.collectAsState().value.sortedByDescending { it.timestamp }
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
                color = Soul.Justice.color,
                modifier = Modifier.padding(bottom = 6.dp)
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
                        .fillMaxWidth()
                        .heightIn(max = 320.dp),
                    state = scrollState,
                    flingBehavior = rememberSnapFlingBehavior(scrollState)
                ) {
                    items(displayResults.size) { index ->
                        HistoryResultRow(result = displayResults[index], now = now)
                        HorizontalDivider(
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
 * Tappable row shown at the bottom of [HistoryCard] when there are more results than [MAX_HISTORY_VISIBLE].
 *
 * @param onClick callback invoked when the row is tapped.
 */
@Composable
private fun ShowAllRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.history_show_all),
            style = MaterialTheme.typography.labelLarge,
            color = Soul.Justice.color,
        )
    }
}

/**
 * Single row inside [HistoryCard] showing the score badge, tool name, severity label, and timestamp.
 *
 * @param result the [ToolResult] to display.
 * @param now the current date/time used as the reference point for [relativeTimestamp].
 */
@Composable
private fun HistoryResultRow(result: ToolResult, now: LocalDateTime) {
    val severity = result.severity()
    val dark    = LocalDarkTheme.current
    val color   = severityColor(severity)
    val onColor = if (!dark || severity == Severity.Severe) Color.White else Color.Black
    val severityLabel = when (severity) {
        Severity.Mild -> stringResource(R.string.severity_mild)
        Severity.Moderate -> stringResource(R.string.severity_moderate)
        Severity.Severe -> stringResource(R.string.severity_severe)
    }
    val scoreText = if (result.score % 1.0 == 0.0) "%.0f".format(result.score)
                    else "%.1f".format(result.score)
    val timestamp = relativeTimestamp(result.timestamp, now)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.size(48.dp),
            shape    = RoundedCornerShape(10.dp),
            colors   = CardDefaults.cardColors(containerColor = color)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text     = scoreText,
                    fontSize = 19.sp,
                    color    = onColor,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text  = result.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text  = "$severityLabel · $timestamp",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Produces a human-readable relative label for [timestamp] compared to [now].
 *
 * Returns "Today at HH:MM" for same-day results, then falls back to days, weeks, months, or years ago.
 *
 * @param timestamp the date/time of the recorded result.
 * @param now the current date/time used as the reference point.
 */
@Composable
private fun relativeTimestamp(timestamp: LocalDateTime, now: LocalDateTime): String {
    val todayDate = now.date
    val resultDate = timestamp.date

    if (resultDate == todayDate) {
        val time = "%02d:%02d".format(timestamp.hour, timestamp.minute)
        return stringResource(R.string.history_today_at, time)
    }

    val daysDiff = (todayDate.toEpochDays() - resultDate.toEpochDays()).toInt().coerceAtLeast(1)
    return when {
        daysDiff < 7 -> pluralStringResource(R.plurals.history_days_ago, daysDiff, daysDiff)
        daysDiff < 30 -> pluralStringResource(R.plurals.history_weeks_ago, daysDiff / 7, daysDiff / 7)
        daysDiff < 365 -> pluralStringResource(R.plurals.history_months_ago, daysDiff / 30, daysDiff / 30)
        else -> pluralStringResource(R.plurals.history_years_ago, daysDiff / 365, daysDiff / 365)
    }
}
