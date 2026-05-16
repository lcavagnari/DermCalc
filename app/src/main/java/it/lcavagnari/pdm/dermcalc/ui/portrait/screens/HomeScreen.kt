package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.navigation.HomeRoute
import it.lcavagnari.pdm.dermcalc.ui.portrait.MainPortraitActivity
import it.lcavagnari.pdm.dermcalc.utils.today
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.Quote
import it.lcavagnari.pdm.dermcalc.models.TextInput
import it.lcavagnari.pdm.dermcalc.models.BmiResult
import it.lcavagnari.pdm.dermcalc.models.ToolsModel
import it.lcavagnari.pdm.dermcalc.ui.shared.component.BorderedCard
import it.lcavagnari.pdm.dermcalc.ui.shared.component.BorderSide
import it.lcavagnari.pdm.dermcalc.ui.shared.component.HistoryCard
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val app = LocalContext.current.applicationContext as Application

    val qm = remember { QuoteModel(app).also { it.updateQuote() } }
    val vm = remember { OnboardingModel(app).also {
        it.finishOnboarding()
        it.updateName("Asriel ")
    } }

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
 * @param quoteModel - view model providing the currently displayed quote.
 * @param onboardingModel - view model providing user profile fields for the welcome message.
 * @param toolsModel - view model providing the stored tool results for [HistoryCard].
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

    // TODO: Remove seed data before release.
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
        toolsModel.addResult(BmiResult(weightKg = 92.0, heightCm = 175.0, score = 30.1, timestamp = today().date.minus(1,
            DateTimeUnit.YEAR).atTime(LocalTime.fromSecondOfDay(0))))
        toolsModel.addResult(BmiResult(weightKg = 78.0, heightCm = 175.0, score = 25.5, timestamp = today()))
    }

    val formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top)
    ) {
        BorderedCard(
            modifier = Modifier.fillMaxWidth(0.9f),
            borderSide = BorderSide.Right,
            borderColor = MaterialTheme.colorScheme.primary,
            borderStrokeWidth = 2.dp,
            cornerRadius = 10.dp,
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = today().toJavaLocalDateTime().format(formatter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = welcomeMessage,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start
                )
            }
        }

        QuoteCard(quoteModel = quoteModel)

        HistoryCard(
            modifier = Modifier.fillMaxWidth(0.9f),
            toolsModel = toolsModel,
            onShowAll = { }
        )
    }
}


/**
 * Card displaying the current dermatology quote from [quoteModel].
 *
 * Shows the quote body in italic and the author right-aligned below it.
 * When no author is available, a tip prompt is shown instead.
 *
 * @param modifier - modifier applied to the [BorderedCard].
 * @param quoteModel - view model providing the [Quote] to display.
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                    maxLines = 2,
                ) else Text(
                    text = quote.author,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}
