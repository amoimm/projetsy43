package com.example.application.ui.bdd

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categorie: String,
    val valeur: String,
    val isDone: Boolean = false,
    val date: String = ""
)