package com.example.application.ui.bdd

import androidx.room.Entity
import androidx.room.PrimaryKey

data class ActiviteSportive(
    val id: Int,
    val categorie: String,
    val valeur: String,
    var isDone: Boolean = false,
    var progress: String = "0"
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
    val activitiesJson: String,
    val frequency: String = "ONCE", // "ONCE", "DAILY", "WEEKLY"
    val targetDays: String = ""      // "Monday,Tuesday..." or "All Week"
) {
    val activities: List<ActiviteSportive> get() {
        if (activitiesJson.isBlank()) return emptyList()
        return try {
            activitiesJson.split(";").mapIndexed { index, s ->
                val parts = s.split(",")
                ActiviteSportive(
                    id = index,
                    categorie = parts[0],
                    valeur = parts[1],
                    isDone = parts[2].toBoolean(),
                    progress = if (parts.size >= 4) parts[3] else "0"
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    val isCompleted: Boolean get() = activities.isNotEmpty() && activities.all { it.isDone }
}

@Entity(tableName = "ads")
data class Ad(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val triggerLocation: String, // "AFTER_LIST", "AFTER_TIME", "AFTER_PUSHUP", "AFTER_RUNNING"
    val triggerValue: String = "",
    val videoUri: String? = null
)
