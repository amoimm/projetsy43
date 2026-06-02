package com.example.application

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // 1. Re-planifier les rappels quotidiens (car WorkManager peut parfois nécessiter un refresh)
            NotificationScheduler.scheduleDailyReminder(context)

            // 2. Afficher une notification immédiate au démarrage avec l'image de Khabib
            NotificationHelper.showReminderNotification(
                context,
                "L'application est prête !",
                "Regarde l'objectif du jour. Ne lâche rien !",
                R.drawable.motivation_khabib
            )
        }
    }
}
