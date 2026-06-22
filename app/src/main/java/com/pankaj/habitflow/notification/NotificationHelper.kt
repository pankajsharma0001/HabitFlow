package com.pankaj.habitflow.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.pankaj.habitflow.MainActivity
import com.pankaj.habitflow.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "habit_reminders"
        const val CHANNEL_NAME = "Habit Reminders"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily habit check-in reminders"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showReminderNotification(habitId: String, habitName: String, habitDesc: String) {
        val clickIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val clickPendingIntent = PendingIntent.getActivity(
            context,
            habitId.hashCode(),
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val completeIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.pankaj.habitflow.ACTION_MARK_COMPLETE"
            putExtra("HABIT_ID", habitId)
            putExtra("NOTIFICATION_ID", habitId.hashCode())
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            habitId.hashCode() + 1,
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Time for $habitName! 💪")
            .setContentText(habitDesc.ifEmpty { "Keep up your daily streak." })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(clickPendingIntent)
            .addAction(
                android.R.drawable.ic_menu_save,
                "Mark Completed",
                completePendingIntent
            )
            .setAutoCancel(true)
            .build()

        notificationManager.notify(habitId.hashCode(), notification)
    }

    fun showEveningReminderNotification(incompleteCount: Int) {
        val clickIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val clickPendingIntent = PendingIntent.getActivity(
            context,
            "evening_reminder".hashCode(),
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val text = if (incompleteCount == 1) {
            "You still have 1 habit left to complete today!"
        } else {
            "You still have $incompleteCount habits left to complete today!"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Finish your day strong! 🌟")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(clickPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify("evening_reminder".hashCode(), notification)
    }
}
