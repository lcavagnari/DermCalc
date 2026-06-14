package it.lcavagnari.pdm.dermcalc.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppSettingsDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: AppSettingsDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.appSettingsDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun upsertAndRead() = runBlocking {
        val entity = AppSettingsEntity(id = 1, isDarkTheme = true, hasSeenOnboarding = true)
        dao.upsert(entity)
        val read = dao.getSettings().first()
        assertNotNull(read)
        assertEquals(true, read?.isDarkTheme)
        assertEquals(true, read?.hasSeenOnboarding)
    }

    @Test
    fun upsertUpdatesExisting() = runBlocking {
        dao.upsert(AppSettingsEntity(id = 1, isDarkTheme = false, hasSeenOnboarding = false))
        dao.upsert(AppSettingsEntity(id = 1, isDarkTheme = true, hasSeenOnboarding = true))
        val read = dao.getSettings().first()
        assertEquals(true, read?.isDarkTheme)
        assertEquals(true, read?.hasSeenOnboarding)
    }
}