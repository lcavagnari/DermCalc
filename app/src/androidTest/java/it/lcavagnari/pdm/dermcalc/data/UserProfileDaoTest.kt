package it.lcavagnari.pdm.dermcalc.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.lcavagnari.pdm.dermcalc.models.Sex
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserProfileDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: UserProfileDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.userProfileDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun upsertAndRead() = runBlocking {
        val entity = UserProfileEntity(
            id = 1,
            fullName = "John Doe",
            dateOfBirth = LocalDate(1990, 1, 15),
            sex = Sex.Male,
            heightCm = 180.0,
            weightKg = 75.0
        )
        dao.upsert(entity)
        val read = dao.getProfile().first()
        assertNotNull(read)
        assertEquals("John Doe", read?.fullName)
        assertEquals(Sex.Male, read?.sex)
    }

    @Test
    fun upsertUpdatesExisting() = runBlocking {
        dao.upsert(UserProfileEntity(id = 1, fullName = "Old Name"))
        dao.upsert(UserProfileEntity(id = 1, fullName = "New Name"))
        val read = dao.getProfile().first()
        assertEquals("New Name", read?.fullName)
    }
}