package it.lcavagnari.pdm.dermcalc.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [AppSettingsEntity].
 * 
 * Manages persistence of application-wide settings.
 */
@Dao
interface AppSettingsDao {
    /**
     * Inserts or replaces the application settings.
     * 
     * @param settings the [AppSettingsEntity] to persist
     */
    @Upsert
    suspend fun upsert(settings: AppSettingsEntity)

    /**
     * Retrieves the application settings.
     * 
     * @return Flow emitting the [AppSettingsEntity] or null if not set
     */
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getSettings(): Flow<AppSettingsEntity?>
}
