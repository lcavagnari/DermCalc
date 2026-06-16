package it.lcavagnari.pdm.dermcalc.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Room database for DermCalc application.
 * 
 * Contains tables for app settings, user profile, and tool calculation results.
 */
@Database(
    entities = [
        AppSettingsEntity::class,
        UserProfileEntity::class,
        ToolResultEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Returns the AppSettings Data Access Object.
     * 
     * @return [AppSettingsDao] for accessing app settings
     */
    abstract fun appSettingsDao(): AppSettingsDao

    /**
     * Returns the UserProfile Data Access Object.
     * 
     * @return [UserProfileDao] for accessing user profile data
     */
    abstract fun userProfileDao(): UserProfileDao

    /**
     * Returns the ToolResult Data Access Object.
     * 
     * @return [ToolResultDao] for accessing tool calculation results
     */
    abstract fun toolResultDao(): ToolResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the singleton instance of the database.
         * 
         * @param context the application context
         * @return the [AppDatabase] instance
         */
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
                db.execSQL("INSERT OR IGNORE INTO app_settings (id, isDarkTheme, hasSeenOnboarding) VALUES (1, 0, 0)")
                db.execSQL("INSERT OR IGNORE INTO user_profile (id) VALUES (1)")
            }
        }
    }
}