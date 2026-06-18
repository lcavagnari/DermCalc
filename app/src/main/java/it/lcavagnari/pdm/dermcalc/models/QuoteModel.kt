/*
 * Copyright (C) 2026 Luca Cavagnari
 *
 * This file is part of DermCalc, final project for the Mobile Device Programming course of Univerità Degli Studi Dell'Insubria.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 */
package it.lcavagnari.pdm.dermcalc.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import it.lcavagnari.pdm.dermcalc.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

/**
 * A single dermatology quote loaded from the string array resource.
 *
 * @property value - the body of the quote.
 * @property author - the attributed author, or null when the author is unknown or blank.
 */
@Serializable
data class Quote(
    val value: String,
    val author: String?
)

/** ViewModel that serves a randomly selected dermatology quote to the home screen. */
class QuoteModel(application: Application) : AndroidViewModel(application) {
    // Loaded from R.array.home_quotes at construction time; never re-fetched from resources.
    private val _quotes = getApplication<Application>().resources.getStringArray(R.array.home_quotes)

    private val _homeQuote = MutableStateFlow(Quote("", ""))

    /** The currently displayed quote; updated by calling [updateQuote]. */
    val homeQuote: StateFlow<Quote> = _homeQuote.asStateFlow()

    /**
     * Picks a random quote from the resource array and updates [homeQuote].
     *
     * Quotes are expected to contain an em-dash separator (" — ") between the body and the author.
     * If no em-dash is found the entire string becomes [Quote.value] and [Quote.author] is null.
     */
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
