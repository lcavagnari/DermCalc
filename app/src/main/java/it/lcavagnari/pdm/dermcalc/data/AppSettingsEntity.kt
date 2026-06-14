package it.lcavagnari.pdm.dermcalc.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val isDarkTheme: Boolean = false,
    val hasSeenOnboarding: Boolean = false
)
