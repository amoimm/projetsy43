package com.example.application.ui.bdd

import kotlinx.coroutines.flow.Flow

class OfflineTaskRepository(private val taskDao: TaskDao) : TaskRepository {
    override fun getAllTasksStream(): Flow<List<Task>> = taskDao.getAllTasks()

    override fun getTaskStream(id: Int): Flow<Task> = taskDao.getTask(id)

    override suspend fun insertTask(task: Task) = taskDao.insert(task)

    override suspend fun deleteTask(task: Task) = taskDao.delete(task)

    override suspend fun updateTask(task: Task) = taskDao.update(task)

    override suspend fun updateTaskStatus(id: Int, isDone: Boolean) = taskDao.updateTaskStatus(id, isDone)

    override suspend fun deleteAllTasks() = taskDao.deleteAllTasks()
}