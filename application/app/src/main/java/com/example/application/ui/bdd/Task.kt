package com.example.application.ui.bdd

import androidx.room.Entity
import androidx.room.PrimaryKey

data class ActiviteSportive(
    val id: Int,
    val categorie: String,
    val valeur: String,
    var isDone: Boolean = false
)

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categorie: String,
    val valeur: String,
    val isDone: Boolean = false,
    val date: String = ""
)

@Entity(tableName = "todo_lists")
data class ToDoList(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val date: String,
    val activitiesJson: String
) {
    val activities: List<ActiviteSportive> get() {
        if (activitiesJson.isBlank()) return emptyList()
        return try {
            activitiesJson.split(";").mapIndexed { index, s ->
                val parts = s.split(",")
                ActiviteSportive(index, parts[0], parts[1], parts[2].toBoolean())
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    val isCompleted: Boolean get() = activities.isNotEmpty() && activities.all { it.isDone }
}
