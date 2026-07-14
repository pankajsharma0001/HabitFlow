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
import com.pankaj.habitflow.data.local.dao.HabitDao
import com.pankaj.habitflow.data.local.dao.HabitRecordDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltWorker
class MonthlySummaryWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val habitDao: HabitDao,
    private val recordDao: HabitRecordDao
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "monthly_summary"
        const val NOTIFICATION_ID = 9002
        const val WORK_NAME = "monthly_summary_worker"
    }

    override suspend fun doWork(): Result {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val today = LocalDate.now()
            val activeHabits = habitDao.getActiveHabits()
            val totalHabits = activeHabits.size

            if (totalHabits == 0) return Result.success()

            val startDateStr = today.minusDays(29).format(formatter)
            val endDateStr = today.format(formatter)
            val completedRecords = recordDao.getCompletedRecordsInRange(startDateStr, endDateStr)

            val budgetSummaryList = mutableListOf<String>()
            val budgetHabits = activeHabits.filter { it.habitType == "BUDGET" }
            for (bh in budgetHabits) {
                val bhRecords = completedRecords.filter { it.habitId == bh.id }
                val totalSpent = bhRecords.sumOf { it.value ?: 0.0 }
                if (totalSpent > 0.0) {
                    val currency = bh.valueUnit ?: "$"
                    val notes = bhRecords.mapNotNull { it.note }.filter { it.isNotBlank() }.distinct()
                    val notesStr = if (notes.isNotEmpty()) " (${notes.joinToString(", ")})" else ""
                    budgetSummaryList.add("- ${bh.name}: $currency${String.format("%.2f", totalSpent)}$notesStr")
                }
            }

            if (budgetSummaryList.isEmpty()) {
                return Result.success() // No monthly spending records to notify
            }

            val title = "📅 Your Monthly Spend Summary"
            val body = buildString {
                append("Here is your spending summary for the past 30 days:\n\n")
                append(budgetSummaryList.joinToString("\n"))
                append("\n\nKeep tracking your budgets in HabitFlow! 📈")
            }

            showMonthlySummaryNotification(title, body)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showMonthlySummaryNotification(title: String, body: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Monthly Summary",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Monthly summary of your budget and spending tracker"
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
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
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
