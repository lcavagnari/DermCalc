package it.lcavagnari.pdm.dermcalc.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

/**
 * User profile data stored in Room database.
 * 
 * @property id unique identifier, defaults to 1 for single profile
 * @property fullName user's full name, nullable
 * @property dateOfBirth user's date of birth, nullable
 * @property sex user's biological sex
 * @property heightCm user's height in centimeters
 * @property weightKg user's weight in kilograms
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String? = null,
    val dateOfBirth: LocalDate? = null,
    val sex: Sex? = null,
    val heightCm: Double = 0.0,
    val weightKg: Double = 0.0
)
