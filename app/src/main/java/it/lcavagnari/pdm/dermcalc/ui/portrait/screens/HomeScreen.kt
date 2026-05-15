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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import it.lcavagnari.pdm.dermcalc.ui.portrait.MainPortraitActivity
import it.lcavagnari.pdm.dermcalc.utils.today
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import it.lcavagnari.pdm.dermcalc.R
import it.lcavagnari.pdm.dermcalc.models.Quote
import it.lcavagnari.pdm.dermcalc.ui.shared.component.BorderedCard
import it.lcavagnari.pdm.dermcalc.ui.shared.component.BorderSide

/**
 * Displays placeholder home content centered within available screen space.
 *
 * @param navController - controller available for future home navigation actions.
 */
@Composable
fun HomeScreen(navController: NavHostController, quoteModel: QuoteModel) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.9f).height(80.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(all = 15.dp),
                horizontalAlignment = Alignment.Start,

            ) {
                Text(
                    text = today().toString(),
                    fontSize = 18.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )

                Text(
                    "Welcome X",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
            }
        }

        QuoteCard(
            modifier = Modifier,
            quoteModel = quoteModel
        )

        Card(
            modifier = Modifier.fillMaxWidth(0.9f).height(80.dp)
        ) {
            Column(
                modifier = Modifier.padding(all = 15.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically)
            ) {
                Text(
                    text = today().toString(),
                    fontSize = 18.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )

                Text(
                    "Welcome X",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
            }
        }
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
            containerColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 15.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            //horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = quote.value.replace("\"",""),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                fontStyle = FontStyle.Italic
            )

            Box(
                modifier = Modifier.fillMaxWidth()
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

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val app = LocalContext.current.applicationContext as Application
    val vm = remember { OnboardingModel(app).also {
        it.finishOnboarding(); it.updateName("Asriel ")
    }}


    val qm = remember { QuoteModel(app) }; qm.randomQuote()

    MainPortraitActivity(onboardingModel = vm, quoteModel = qm, startingDestination = HomeRoute)
}
