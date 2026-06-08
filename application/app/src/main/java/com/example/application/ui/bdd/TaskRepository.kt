package com.example.application.ui.bdd

import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasksStream(): Flow<List<Task>>
    fun getTaskStream(id: Int): Flow<Task>
    suspend fun insertTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun updateTaskStatus(id: Int, isDone: Boolean)
    suspend fun deleteAllTasks()

    val allToDoLists: Flow<List<ToDoList>>
    suspend fun insertToDoList(list: ToDoList)
    suspend fun deleteToDoList(list: ToDoList)
}
