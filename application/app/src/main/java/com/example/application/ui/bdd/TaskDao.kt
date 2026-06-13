package com.example.application.ui.bdd

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * from tasks WHERE id = :id")
    fun getTask(id: Int): Flow<Task>

    @Query("SELECT * from tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("UPDATE tasks SET isDone = :isDone WHERE id = :id")
    suspend fun updateTaskStatus(id: Int, isDone: Boolean)

    // ToDoLists
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDoList(toDoList: ToDoList)

    @Query("SELECT * FROM todo_lists")
    fun getAllLists(): Flow<List<ToDoList>>

    @Delete
    suspend fun deleteToDoList(toDoList: ToDoList)

    // Ads
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAd(ad: Ad)

    @Query("SELECT * FROM ads ORDER BY triggerLocation ASC")
    fun getAllAds(): Flow<List<Ad>>

    @Delete
    suspend fun deleteAd(ad: Ad)
}
