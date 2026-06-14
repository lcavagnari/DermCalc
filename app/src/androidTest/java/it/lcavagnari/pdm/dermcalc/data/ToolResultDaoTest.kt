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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ToolResultDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: ToolResultDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.toolResultDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndRead() = runBlocking {
        val entity = ToolResultEntity(toolName = "PASI", score = 12.5, detailsJson = "{""{"score":12.5}""}")
        dao.upsert(entity)
        val all = dao.getAll().first()
        assertEquals(1, all.size)
        assertEquals("PASI", all[0].toolName)
        assertEquals(12.5, all[0].score, 0.001)
    }

    @Test
    fun deleteById() = runBlocking {
        val entity = ToolResultEntity(toolName = "BMI", score = 22.9, detailsJson = "{""{"score":22.9}""}")
        dao.upsert(entity)
        val inserted = dao.getAll().first()
        assertTrue(inserted.isNotEmpty())
        val id = inserted.first().id
        dao.deleteById(id)
        val after = dao.getAll().first()
        assertTrue(after.isEmpty())
    }

    @Test
    fun deleteAll() = runBlocking {
        dao.upsert(ToolResultEntity(toolName = "PASI", score = 10.0, detailsJson = "{""{}""}"))
        dao.upsert(ToolResultEntity(toolName = "EASI", score = 20.0, detailsJson = "{""{}""}"))
        dao.deleteAll()
        val after = dao.getAll().first()
        assertTrue(after.isEmpty())
    }

    @Test
    fun getAllOrderedById() = runBlocking {
        dao.upsert(ToolResultEntity(toolName = "A", score = 1.0, detailsJson = "{""{}""}"))
        dao.upsert(ToolResultEntity(toolName = "B", score = 2.0, detailsJson = "{""{}""}"))
        dao.upsert(ToolResultEntity(toolName = "C", score = 3.0, detailsJson = "{""{}""}"))
        val all = dao.getAll().first()
        assertEquals(3, all.size)
        // IDs should be ascending (auto-generated), ORDER BY id DESC means most recent first
        assertTrue(all[0].id > all[2].id)
    }
}