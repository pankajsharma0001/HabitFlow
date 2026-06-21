package com.pankaj.habitflow.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getStringExtra("HABIT_ID") ?: return
        val habitName = intent.getStringExtra("HABIT_NAME") ?: "Habit Check-in"
        val habitDesc = intent.getStringExtra("HABIT_DESC") ?: ""
        val reminderMinutes = intent.getIntExtra("REMINDER_MINUTES", -1)

        Log.d("ReminderReceiver", "Alarm received for habit $habitId: $habitName")

        notificationHelper.showReminderNotification(habitId, habitName, habitDesc)

        if (reminderMinutes != -1) {
            alarmScheduler.scheduleAlarm(habitId, habitName, habitDesc, reminderMinutes)
        }
    }
}
