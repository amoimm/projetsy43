package com.example.application.ui.bdd

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ActivityCategory {
    PUSHUP, PULLUP, SQUAT, RUNNING
}

enum class Frequency {
    ONCE, DAILY, WEEKLY
}

enum class AdTriggerLocation {
    AFTER_LIST, AFTER_TIME, AFTER_PUSHUP, AFTER_RUNNING, AFTER_DELETE
}

data class ActiviteSportive(
    val id: Int,
    val categorie: ActivityCategory,
    val valeur: String,
    var isDone: Boolean = false,
    var progress: String = "0"
)

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categorie: ActivityCategory,
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
    val frequency: Frequency = Frequency.ONCE,
    val targetDays: String = ""
) {
    val activities: List<ActiviteSportive> get() {
        if (activitiesJson.isBlank()) return emptyList()
        return try {
            activitiesJson.split(";").mapIndexed { index, s ->
                val parts = s.split(",")
                ActiviteSportive(
                    id = index,
                    categorie = try { 
                        ActivityCategory.valueOf(parts[0].uppercase().replace(" ", "")) 
                    } catch (e: Exception) { 
                        ActivityCategory.PUSHUP 
                    },
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

@Entity(tableName = "ad_metrics")
data class AdMetric(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val adId: Int,
    val userName: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ads")
data class Ad(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val triggerLocation: String, // Comma separated names of AdTriggerLocation
    val triggerValue: String = "",
    val videoUri: String? = null
)
