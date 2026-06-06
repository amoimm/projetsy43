package com.example.application

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {
    private const val CHANNEL_ID = "exercise_reminder"

    fun showReminderNotification(context: Context, title: String? = null, message: String? = null, imageResId: Int? = null) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Exercise Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminds you to do your daily exercises"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // On utilise l'image fournie ou le launcher par défaut
        val imageToDisplay = imageResId ?: R.mipmap.ic_launcher
        val bitmap = BitmapFactory.decodeResource(context.resources, imageToDisplay)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(bitmap)
            .setContentTitle(title ?: "Last chance to save your streak!")
            .setContentText(message ?: "Practice your exercises now or lose your progress!")
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(bitmap)
                .bigLargeIcon(null as android.graphics.Bitmap?))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}
