package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.navigation.HomeRoute
import it.lcavagnari.pdm.dermcalc.navigation.ToolsRoute
import it.lcavagnari.pdm.dermcalc.ui.portrait.MainPortraitActivity
import it.lcavagnari.pdm.dermcalc.utils.today
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.Quote
import it.lcavagnari.pdm.dermcalc.models.TextInput
import it.lcavagnari.pdm.dermcalc.models.BmiResult
import it.lcavagnari.pdm.dermcalc.models.BsaResult
import it.lcavagnari.pdm.dermcalc.models.EasiResult
import it.lcavagnari.pdm.dermcalc.models.PasiResult
import it.lcavagnari.pdm.dermcalc.models.ToolResult
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.ui.shared.component.BorderedCard
import it.lcavagnari.pdm.dermcalc.ui.shared.component.BorderSide
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val MAX_HISTORY_VISIBLE = 6

private enum class Severity { Mild, Moderate, Severe }

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

private fun Severity.color(): Color = when (this) {
    Severity.Mild -> Color(0xFF4CAF50)
    Severity.Moderate -> Color(0xFFFFA726)
    Severity.Severe -> Color(0xFFEF5350)
}


@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val app = LocalContext.current.applicationContext as Application

    val qm = remember { QuoteModel(app).also { it.updateQuote() } };
    val vm = remember { OnboardingModel(app).also {
        it.finishOnboarding(); it.updateName("Asriel ")
    }}

    val tm = remember { ToolsModel(app).also {
    }}


    MainPortraitActivity(
        onboardingModel = vm, quoteModel = qm,
        toolsModel = tm,
        startingDestination = HomeRoute)
}

/**
 * Displays placeholder home content centered within available screen space.
 *
 * @param navController - controller available for future home navigation actions.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavHostController,
    quoteModel: QuoteModel,
    onboardingModel: OnboardingModel,
    toolsModel: ToolsModel
) {
    val fullNameField: TextInput = onboardingModel.fields.collectAsState().value[0] as TextInput
    val welcomeMessage = stringResource(R.string.nav_home_subtitle)+ ", " +fullNameField.value.split(' ')[0]

    LaunchedEffect(Unit) {
        toolsModel.addResult(BmiResult(weightKg = 70.0, heightCm = 175.0, score = 22.9))
        toolsModel.addResult(BmiResult(weightKg = 85.0, heightCm = 175.0, score = 27.8, timestamp = today().date.minus(3,
            DateTimeUnit.DAY).atTime(LocalTime.fromSecondOfDay(0))))
        toolsModel.addResult(BmiResult(weightKg = 110.0, heightCm = 175.0, score = 35.9, timestamp = today().date.minus(10,
            DateTimeUnit.DAY).atTime(LocalTime.fromSecondOfDay(0))))
        toolsModel.addResult(BmiResult(weightKg = 110.0, heightCm = 175.0, score = 35.9, timestamp = today().date.minus(10,
            DateTimeUnit.WEEK).atTime(LocalTime.fromSecondOfDay(0))))
        toolsModel.addResult(BmiResult(weightKg = 110.0, heightCm = 175.0, score = 35.9, timestamp = today().date.minus(10,
            DateTimeUnit.MONTH).atTime(LocalTime.fromSecondOfDay(0))))
    }

    val formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(80.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.secondary
            ),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(all = 15.dp),
                horizontalAlignment = Alignment.Start,

            ) {
                Text(
                    text = today().toJavaLocalDateTime().format(formatter),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = welcomeMessage,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
            }
        }

        QuoteCard(quoteModel = quoteModel)

        HistoryCard(
            toolsModel = toolsModel,
            onShowAll = { navController.navigate(ToolsRoute) }
        )
    }
}


@Composable
fun QuoteCard(modifier: Modifier = Modifier, quoteModel: QuoteModel) {
    val quote: Quote = quoteModel.homeQuote.collectAsState().value

    BorderedCard(
        modifier = modifier.fillMaxWidth(0.9f),
        borderSide = BorderSide.Left,
        borderStrokeWidth = 4.dp,
        cornerRadius = 12.dp,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 15.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = quote.value,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                fontStyle = FontStyle.Italic
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
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Start,
                    fontStyle = FontStyle.Italic,
                    maxLines = 2

                ) else Text(
                    text = quote.author,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}


@Composable
fun HistoryCard(toolsModel: ToolsModel, onShowAll: () -> Unit) {
    val results = toolsModel.toolsResult.collectAsState().value
    val now = remember { today() }
    val displayResults = results.takeLast(MAX_HISTORY_VISIBLE)
    val hasMore = results.size > MAX_HISTORY_VISIBLE

    Card(
        modifier = Modifier.fillMaxWidth(0.9f),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.65f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = stringResource(R.string.home_history_title).uppercase(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 15.sp,
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
                displayResults.forEachIndexed { index, result ->
                    HistoryResultRow(result = result, now = now)
                    if (index < displayResults.lastIndex || hasMore) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 2.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
                if (hasMore) {
                    ShowAllRow(onClick = onShowAll)
                }
            }
        }
    }
}

@Composable
private fun HistoryResultRow(result: ToolResult, now: LocalDateTime) {
    val severity = result.severity()
    val color = severity.color()
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
            modifier = Modifier.size(56.dp),
            colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
            border = BorderStroke(1.5.dp, color)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = scoreText,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = result.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(
                text = "$severityLabel · $timestamp",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

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
            color = MaterialTheme.colorScheme.primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}


