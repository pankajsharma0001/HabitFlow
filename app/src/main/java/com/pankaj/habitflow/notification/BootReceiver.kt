package com.pankaj.habitflow.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.pankaj.habitflow.data.local.ThemePreferences
import com.pankaj.habitflow.data.local.dao.HabitDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var habitDao: HabitDao

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var themePreferences: ThemePreferences

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            Log.d("BootReceiver", "Rescheduling alarms after boot/update")

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val habitsWithReminders = habitDao.getHabitsWithReminders()
                    Log.d("BootReceiver", "Found ${habitsWithReminders.size} habits to reschedule")
                    for (entity in habitsWithReminders) {
                        entity.reminderTimeMinutes?.let { minutes ->
                            alarmScheduler.scheduleAlarm(
                                habitId = entity.id,
                                habitName = entity.name,
                                habitDesc = entity.description,
                                reminderTimeMinutes = minutes
                            )
                        }
                    }

                    val eveningReminderEnabled = themePreferences.eveningReminderFlow.first()
                    if (eveningReminderEnabled) {
                        alarmScheduler.scheduleEveningReminder(1200)
                    }
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error rescheduling alarms", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
