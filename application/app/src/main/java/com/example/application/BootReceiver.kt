package com.example.application

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // 1. Reschedule daily reminders
            NotificationScheduler.scheduleDailyReminder(context)

            // 2. Display a random notification at startup
            NotificationHelper.showRandomNotification(context)
        }
    }
}
