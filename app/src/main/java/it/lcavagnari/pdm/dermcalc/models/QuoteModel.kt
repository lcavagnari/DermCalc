package it.lcavagnari.pdm.dermcalc.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import it.lcavagnari.pdm.dermcalc.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val value: String,
    val author: String?
)

class QuoteModel(application: Application) : AndroidViewModel(application) {
    private val _homeQuote = MutableStateFlow(Quote("", ""))
    val homeQuote: StateFlow<Quote> = _homeQuote.asStateFlow()

    fun updateQuote(quote: Quote) { _homeQuote.value = quote }

    fun randomQuote() {
        val resources = getApplication<Application>().resources
        val quotes = resources.getStringArray(R.array.home_quotes)
        val quoteRaw = quotes.random().split(" — ")
        _homeQuote.value = Quote(
            value = quoteRaw[0],
            author = if (quoteRaw.size > 1 && quoteRaw[1].isNotBlank()
                            && quoteRaw[1] != "Unknown") quoteRaw[1] else null
        )
    }
}