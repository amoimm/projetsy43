package com.example.application.ui.bdd

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ActivityCategory {
    PUSHUP, PULLUP, SQUAT, RUNNING
}

enum class Frequency {
    ONCE, DAILY, WEEKLY
}

enum class AdTriggerLocation {
    AFTER_LIST, AFTER_PUSHUP, AFTER_RUNNING, AFTER_DELETE
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

@Entity(
    tableName = "todo_lists",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class ToDoList(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
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

@Entity(
    tableName = "ad_metrics",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class AdMetric(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val adId: Int,
    val userId: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "ads",
    foreignKeys = [
        ForeignKey(
            entity = Partner::class,
            parentColumns = ["id"],
            childColumns = ["partnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("partnerId")]
)
data class Ad(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val partnerId: Int,
    val title: String,
    val content: String,
    val triggerLocation: String, // Comma separated names of AdTriggerLocation
    val triggerValue: String = "",
    val videoUri: String? = null
)

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val mdp: String,
    val name: String = "",
    val age: String = "",
    val weight: String = "",
    val height: String = "",
    val maxPushups: String = "0",
    val maxPullups: String = "0",
    val maxSquats: String = "0",
    val maxRunningKm: String = "0.0",
    val lastExerciseDate: Long = 0L,
    val lastMotivationDate: String = "",
    val lastMotivationLevel: Float = 0.5f,
    val hasCompletedOnboarding: Boolean = false
)

@Entity(
    tableName = "partners",
    indices = [Index(value = ["username"], unique = true)]
)
data class Partner(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val mdp: String,
    val lastName: String = "",
    val firstName: String = "",
    val company: String = ""
)
