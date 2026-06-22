package com.pankaj.habitflow.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.pankaj.habitflow.domain.repository.HabitRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var repository: HabitRepository

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == "com.pankaj.habitflow.ACTION_MARK_COMPLETE") {
            val habitId = intent.getStringExtra("HABIT_ID") ?: return
            val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)

            Log.d("ReminderReceiver", "Mark completed action triggered for habit $habitId")

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val today = LocalDate.now()
                    val isCompleted = repository.isHabitCompletedOn(habitId, today)
                    if (!isCompleted) {
                        repository.toggleHabitCompletion(habitId, today)
                    }
                    
                    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(notificationId)
                } catch (e: Exception) {
                    Log.e("ReminderReceiver", "Error marking habit completed from notification", e)
                } finally {
                    pendingResult.finish()
                }
            }
            return
        }

        val isEvening = intent.getBooleanExtra("IS_EVENING_REMINDER", false)
        val reminderMinutes = intent.getIntExtra("REMINDER_MINUTES", -1)

        if (isEvening) {
            Log.d("ReminderReceiver", "Evening reminder alarm triggered")
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val stats = repository.getDayStats(LocalDate.now())
                    val incomplete = stats.totalHabits - stats.completedHabits
                    if (incomplete > 0) {
                        notificationHelper.showEveningReminderNotification(incomplete)
                    }
                    if (reminderMinutes != -1) {
                        alarmScheduler.scheduleEveningReminder(reminderMinutes)
                    }
                } catch (e: Exception) {
                    Log.e("ReminderReceiver", "Error checking evening habits", e)
                } finally {
                    pendingResult.finish()
                }
            }
            return
        }

        val habitId = intent.getStringExtra("HABIT_ID") ?: return
        val habitName = intent.getStringExtra("HABIT_NAME") ?: "Habit Check-in"
        val habitDesc = intent.getStringExtra("HABIT_DESC") ?: ""

        Log.d("ReminderReceiver", "Alarm received for habit $habitId: $habitName")

        notificationHelper.showReminderNotification(habitId, habitName, habitDesc)

        if (reminderMinutes != -1) {
            alarmScheduler.scheduleAlarm(habitId, habitName, habitDesc, reminderMinutes)
        }
    }
}
