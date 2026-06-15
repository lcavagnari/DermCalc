package it.lcavagnari.pdm.dermcalc.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Application settings stored in Room database.
 * 
 * @property id unique identifier, defaults to 1 for single settings record
 * @property isDarkTheme whether dark theme is enabled
 * @property hasSeenOnboarding whether user has completed onboarding
 */
@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val isDarkTheme: Boolean = false,
    val hasSeenOnboarding: Boolean = false
)
