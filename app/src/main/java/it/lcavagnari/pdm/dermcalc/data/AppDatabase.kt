@file:OptIn(kotlin.time.ExperimentalTime::class)

package it.lcavagnari.pdm.dermcalc.data

import kotlin.time.ExperimentalTime

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AppSettingsEntity::class,
        UserProfileEntity::class,
        ToolResultEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun toolResultDao(): ToolResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dermcalc.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(seedCallback)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val seedCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        database.appSettingsDao().upsert(
                            AppSettingsEntity(id = 1, isDarkTheme = false, hasSeenOnboarding = false)
                        )
                    }
                }
            }
        }
    }
}


