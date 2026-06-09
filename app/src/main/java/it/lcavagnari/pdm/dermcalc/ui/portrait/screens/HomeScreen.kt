package it.lcavagnari.pdm.dermcalc.ui.portrait.screens

import android.app.Application
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import it.lcavagnari.pdm.dermcalc.ui.component.BorderSide
import it.lcavagnari.pdm.dermcalc.ui.component.BorderedCard
import it.lcavagnari.pdm.dermcalc.ui.component.HistoryCard
import it.lcavagnari.pdm.dermcalc.ui.portrait.MainPortraitActivity
import it.lcavagnari.pdm.dermcalc.ui.theme.DermCalcTheme
import it.lcavagnari.pdm.dermcalc.utils.today
import kotlinx.datetime.number
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val context = LocalContext.current
    val app = object : Application() { init { attachBaseContext(context) } }

    val qm = remember { QuoteModel(app) }.also { it.updateQuote() }
    val vm = remember { OnboardingModel(app) }.also {
        it.finishOnboarding(); it.updateName("Asriel ")
    }
    val tm = remember { ToolsModel(app) }

    DermCalcTheme { MainPortraitActivity(onboardingModel = vm, quoteModel = qm, toolsModel = tm) }
}

/**
 * Home screen. Centered column of three stacked cards at 90% screen width.
 *
 * Layout (top → bottom):
 * - Date + first-name welcome greeting — [BorderedCard] with a right-side primary-color accent.
 * - [QuoteCard] — italic dermatology quote with author attribution; left-side tertiary accent.
 * - [HistoryCard] — snap-fling [androidx.compose.foundation.lazy.LazyColumn] of the most recent tool results.
 *
 * @param navController controller available for future home navigation actions.
 * @param quoteModel view model providing the currently displayed quote.
 * @param onboardingModel view model providing user profile fields for the welcome message.
 * @param toolsModel view model providing the stored tool results for [HistoryCard].
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
    val dateText = java.text.SimpleDateFormat("EEEE dd MMMM", Locale.getDefault()).format(
        java.util.Calendar.getInstance().apply {
            set(todayDate.year, todayDate.month.number - 1, todayDate.day)
        }.time
    )

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
 * @param modifier modifier applied to the [BorderedCard].
 * @param quoteModel view model providing the [Quote] to display.
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
