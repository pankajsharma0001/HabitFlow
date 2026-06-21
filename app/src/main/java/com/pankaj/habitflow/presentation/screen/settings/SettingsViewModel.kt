package com.pankaj.habitflow.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pankaj.habitflow.data.local.ThemePreferences
import com.pankaj.habitflow.notification.AlarmScheduler
import com.pankaj.habitflow.presentation.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themePreferences: ThemePreferences,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = themePreferences.themeModeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    val eveningReminderEnabled: StateFlow<Boolean> = themePreferences.eveningReminderFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val eveningReminderTime: StateFlow<Int> = themePreferences.eveningReminderTimeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 1200
        )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themePreferences.setThemeMode(mode)
        }
    }

    fun setEveningReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            themePreferences.setEveningReminderEnabled(enabled)
            if (enabled) {
                alarmScheduler.scheduleEveningReminder(eveningReminderTime.value)
            } else {
                alarmScheduler.cancelEveningReminder()
            }
        }
    }

    fun setEveningReminderTime(timeMinutes: Int) {
        viewModelScope.launch {
            themePreferences.setEveningReminderTime(timeMinutes)
            if (eveningReminderEnabled.value) {
                alarmScheduler.scheduleEveningReminder(timeMinutes)
            }
        }
    }
}
