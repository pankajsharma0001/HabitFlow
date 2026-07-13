package com.pankaj.habitflow.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pankaj.habitflow.MainActivity
import com.pankaj.habitflow.R
import com.pankaj.habitflow.data.local.dao.HabitDao
import com.pankaj.habitflow.data.local.dao.HabitRecordDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltWorker
class WeeklySummaryWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val habitDao: HabitDao,
    private val recordDao: HabitRecordDao
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "weekly_summary"
        const val NOTIFICATION_ID = 9001
        const val WORK_NAME = "weekly_summary_worker"
    }

    override suspend fun doWork(): Result {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val today = LocalDate.now()
            val activeHabits = habitDao.getActiveHabits()
            val totalHabits = activeHabits.size

            if (totalHabits == 0) return Result.success()

            // Calculate stats for the past 7 days
            var totalCompletions = 0
            var totalPossible = 0

            for (dayOffset in 0L until 7L) {
                val date = today.minusDays(dayOffset)
                val dateStr = date.format(formatter)
                val completedIds = recordDao.getCompletedHabitIdsForDate(dateStr).toSet()
                totalCompletions += activeHabits.count { it.id in completedIds }
                totalPossible += totalHabits
            }

            val completionRate = if (totalPossible > 0) {
                (totalCompletions * 100) / totalPossible
            } else 0

            // Find the habit with the highest streak (best performer this week)
            val bestHabit = activeHabits.maxByOrNull { habit ->
                // Count consecutive completed days ending today
                var streak = 0
                for (i in 0L until 7L) {
                    val dateStr = today.minusDays(i).format(formatter)
                    val completed = recordDao.getCompletedHabitIdsForDate(dateStr)
                    if (habit.id in completed) streak++ else break
                }
                streak
            }

            val title = "📊 Your Weekly Summary"
            val body = buildString {
                append("This week: $totalCompletions/$totalPossible completed ($completionRate%)")
                if (bestHabit != null) {
                    append("\n⭐ Top habit: ${bestHabit.name}")
                }
                if (completionRate >= 80) {
                    append("\n🔥 Amazing consistency! Keep it up!")
                } else if (completionRate >= 50) {
                    append("\n💪 Good progress! Push for more this week!")
                } else {
                    append("\n🌱 Room to grow. Small steps matter!")
                }
            }

            showWeeklySummaryNotification(title, body)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showWeeklySummaryNotification(title: String, body: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.weekly_summary_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.weekly_summary_channel_desc)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val clickIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
