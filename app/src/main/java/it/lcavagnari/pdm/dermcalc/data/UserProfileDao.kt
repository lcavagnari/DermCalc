package it.lcavagnari.pdm.dermcalc.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [UserProfileEntity].
 * 
 * Manages persistence of the single user profile record.
 */
@Dao
interface UserProfileDao {
    /**
     * Inserts or replaces the user profile.
     * 
     * @param profile the [UserProfileEntity] to persist
     */
    @Upsert
    suspend fun upsert(profile: UserProfileEntity)

    /**
     * Retrieves the user profile.
     * 
     * @return Flow emitting the [UserProfileEntity] or null if not set
     */
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<UserProfileEntity?>
}
