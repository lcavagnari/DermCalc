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