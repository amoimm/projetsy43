package com.example.application

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlin.random.Random

object NotificationHelper {
    private const val CHANNEL_ID = "exercise_reminder"

    // Liste de textes interchangeables
    private val motivationTexts = listOf(
        "Dernière chance pour ta série !",
        "Khabib te regarde...",
        "Le sport n'attend pas.",
        "Bouge-toi maintenant !",
        "Ta progression est en danger.",
        "Un petit effort pour un grand résultat.",
        "Pas d'excuses aujourd'hui.",
        "Deviens la meilleure version de toi-même.",
        "L'entraînement du jour t'attend.",
        "Reste discipliné, reste fort."
    )

    // List of images to put in the notifications
    private val motivationImages = listOf(
        R.drawable.motivation1,
        R.drawable.motivation2,
        R.drawable.motivation3,
        R.mipmap.ic_launcher // Default image if no other image is found
    )

    fun showRandomNotification(context: Context) {
        // Mix them up and take two different texts
        val shuffledTexts = motivationTexts.shuffled()
        val title = shuffledTexts[0]
        val message = shuffledTexts[1]
        
        // We choose a random image
        val randomImage = motivationImages[Random.nextInt(motivationImages.size)]
        
        showReminderNotification(context, title, message, randomImage)
    }

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

        val imageToDisplay = imageResId ?: R.mipmap.ic_launcher
        val bitmap = try {
            BitmapFactory.decodeResource(context.resources, imageToDisplay)
        } catch (e: Exception) {
            BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(bitmap)
            .setContentTitle(title ?: "Don't stop now")
            .setContentText(message ?: "Practice your exercises or lose your progress!")
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(bitmap)
                .bigLargeIcon(null as android.graphics.Bitmap?))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}
