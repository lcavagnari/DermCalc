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
package it.lcavagnari.pdm.dermcalc.data

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import it.lcavagnari.pdm.dermcalc.models.BodyScanModel
import it.lcavagnari.pdm.dermcalc.models.OnboardingModel
import it.lcavagnari.pdm.dermcalc.models.QuoteModel
import it.lcavagnari.pdm.dermcalc.models.ToolsModel

/**
 * Factory for creating DermCalc ViewModels with proper dependency injection.
 * 
 * @param database the [AppDatabase] instance for data access
 * @param context the application context for resource access
 */
class DermCalcViewModelFactory(
    private val database: AppDatabase,
    private val context: Context
) : ViewModelProvider.Factory {

    /**
     * Creates a ViewModel instance of the specified class.
     * 
     * @param modelClass The ViewModel class to instantiate
     * @return An instance of the requested ViewModel class
     * @throws IllegalArgumentException if the requested ViewModel class is not supported
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ToolsModel::class.java) ->
                ToolsModel(database.toolResultDao()) as T

            modelClass.isAssignableFrom(OnboardingModel::class.java) ->
                OnboardingModel(database.userProfileDao(), database.appSettingsDao()) as T

            modelClass.isAssignableFrom(QuoteModel::class.java) ->
                QuoteModel(context.applicationContext as Application) as T

            modelClass.isAssignableFrom(BodyScanModel::class.java) ->
                BodyScanModel(context.applicationContext as Application) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
