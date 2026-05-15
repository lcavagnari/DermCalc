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
    private val _quotes = getApplication<Application>().resources.getStringArray(R.array.home_quotes)

    private val _homeQuote = MutableStateFlow(Quote("", ""))
    val homeQuote: StateFlow<Quote> = _homeQuote.asStateFlow()

    fun updateQuote() {
        val full = _quotes.random()
        val lastEmDash = full.lastIndexOf(" — ")

        if (lastEmDash != -1) {
            _homeQuote.value = Quote(
                value = full.substring(0, lastEmDash),
                author = full.substring(lastEmDash + 3).let { a ->
                    if (a.isNotBlank() && a != "Unknown") a else null
                }
            )

        } else _homeQuote.value = Quote(value = full, author = null)
    }
}