package it.lcavagnari.pdm.dermcalc.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import it.lcavagnari.pdm.dermcalc.R
import kotlinx.serialization.Serializable


@Composable
public fun getRandomQuote(): Quote {
    val quotes = stringArrayResource(id = R.array.home_quotes)
    val quoteRaw = quotes.random().split(" — ")

    return Quote(
        quote = quoteRaw[0],
        author = if (quoteRaw.size > 1 && quoteRaw[1].isNotBlank()
                        && quoteRaw[1] != "Unknown") quoteRaw[1] else null
    )
}

@Serializable
data class Quote(
    val quote: String,
    val author: String?
)