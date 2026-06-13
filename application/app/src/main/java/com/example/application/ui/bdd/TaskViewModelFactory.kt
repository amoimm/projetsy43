package com.example.application.ui.bdd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.application.ui.TaskApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val application = (this[APPLICATION_KEY] as TaskApplication)
            TaskViewModel(application.container.taskRepository)
        }
    }
}