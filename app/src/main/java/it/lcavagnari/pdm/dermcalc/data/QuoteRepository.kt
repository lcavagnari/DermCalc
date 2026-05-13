package it.lcavagnari.pdm.dermcalc.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import it.lcavagnari.pdm.dermcalc.R
import kotlinx.serialization.Serializable
import kotlin.random.Random



@Composable
fun getRandomQuote(): Quote {
    val quotes = stringArrayResource(id = R.array.home_quotes)
    val quoteRaw = quotes.random().split(" — ")

    return Quote(
        quote = quoteRaw[0],
        author = if (quoteRaw.size > 1 && quoteRaw[1].isNotBlank() && !quoteRaw[1].equals("Unknown")) quoteRaw[1] else null
    )
}

@Serializable
data class Quote(
    val quote: String,
    val author: String?
)