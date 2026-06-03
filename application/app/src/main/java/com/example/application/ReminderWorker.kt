package com.example.application

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import java.util.Calendar

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val userProfile = applicationContext.userProfileFlow.first()
        val lastExerciseDate = userProfile.lastExerciseDate
        
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // If last exercise was before today, notify with random content
        if (lastExerciseDate < today) {
            NotificationHelper.showRandomNotification(applicationContext)
        }

        return Result.success()
    }
}
