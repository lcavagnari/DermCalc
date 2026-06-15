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
