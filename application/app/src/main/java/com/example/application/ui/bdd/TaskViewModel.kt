package com.example.application.ui.bdd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    val taskUiState: StateFlow<List<Task>> = taskRepository.getAllTasksStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    fun updateTaskStatus(id: Int, isDone: Boolean) {
        viewModelScope.launch {
            taskRepository.updateTaskStatus(id, isDone)
        }
    }

    val allToDoLists = repository.allToDoLists.asLiveData()

    fun insertToDoList(list: ToDoList) = viewModelScope.launch {
        repository.insertToDoList(list)
    }
}