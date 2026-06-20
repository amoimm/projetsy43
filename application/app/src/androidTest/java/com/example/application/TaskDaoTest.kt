package com.example.application

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.application.ui.bdd.TaskDao
import com.example.application.ui.bdd.TaskDatabase
import com.example.application.ui.bdd.ToDoList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
    private lateinit var taskDao: TaskDao
    private lateinit var db: TaskDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, TaskDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        taskDao = db.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun daoInsertAndGetLists() = runBlocking {
        val list = ToDoList(
            id = 1,
            title = "Test List",
            date = "01/01/2024",
            activitiesJson = "Pushup,10,false,0",
            frequency = "ONCE"
        )
        taskDao.insertToDoList(list)
        val allLists = taskDao.getAllLists().first()
        assertEquals(allLists[0].title, "Test List")
        assertEquals(allLists[0].activities.size, 1)
    }

    @Test
    @Throws(Exception::class)
    fun daoDeleteList() = runBlocking {
        val list = ToDoList(
            id = 2,
            title = "To Delete",
            date = "01/01/2024",
            activitiesJson = "Running,5,false,0"
        )
        taskDao.insertToDoList(list)
        taskDao.deleteToDoList(list)
        val allLists = taskDao.getAllLists().first()
        assertTrue(allLists.isEmpty())
    }
}
