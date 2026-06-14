package it.lcavagnari.pdm.dermcalc.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import it.lcavagnari.pdm.dermcalc.models.Sex
import kotlinx.datetime.LocalDate

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String? = null,
    val dateOfBirth: LocalDate? = null,
    val sex: Sex = Sex.Other,
    val heightCm: Double = 0.0,
    val weightKg: Double = 0.0
)
