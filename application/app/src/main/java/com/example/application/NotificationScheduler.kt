package com.example.application

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    /**
     * Schedule all reminders for the day: 10 a.m., 1 p.m., 4 p.m., and 7 p.m.
     */
    fun scheduleDailyReminder(context: Context) {
        val reminderHours = listOf(10, 13, 16, 19)
        
        reminderHours.forEach { hour ->
            scheduleReminderForHour(context, hour)
        }
    }

    private fun scheduleReminderForHour(context: Context, hour: Int) {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        // If it's already past that time, we'll start tomorrow
        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        val initialDelay = calendar.timeInMillis - now

        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        // We use a unique name for each hour to prevent them from overwriting each other
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "exercise_reminder_$hour",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
