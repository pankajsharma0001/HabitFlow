package com.pankaj.habitflow.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.google.firebase.auth.FirebaseUser
import com.pankaj.habitflow.data.local.ThemePreferences
import com.pankaj.habitflow.data.sync.SyncScheduler
import com.pankaj.habitflow.domain.repository.AuthRepository
import com.pankaj.habitflow.notification.AlarmScheduler
import com.pankaj.habitflow.presentation.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themePreferences: ThemePreferences,
    private val alarmScheduler: AlarmScheduler,
    private val authRepository: AuthRepository,
    private val syncScheduler: SyncScheduler
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

    // Cloud Sync Preferences & State
    val currentUser: StateFlow<FirebaseUser?> = authRepository.currentUserFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = authRepository.currentUser
        )

    val isSyncEnabled: StateFlow<Boolean> = themePreferences.syncEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val lastSyncTimestamp: StateFlow<Long> = themePreferences.lastSyncTimestampFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0L
        )

    val isSyncing: StateFlow<Boolean> = combine(
        syncScheduler.syncWorkInfoFlow,
        syncScheduler.manualSyncWorkInfoFlow
    ) { periodicInfo, manualInfo ->
        periodicInfo?.state == WorkInfo.State.RUNNING || manualInfo?.state == WorkInfo.State.RUNNING
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
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

    fun setSyncEnabled(enabled: Boolean, onTriggerSignIn: () -> Unit) {
        viewModelScope.launch {
            if (enabled) {
                if (authRepository.isSignedIn()) {
                    themePreferences.setSyncEnabled(true)
                    syncScheduler.schedulePeriodicSync()
                    syncScheduler.requestImmediateSync()
                } else {
                    onTriggerSignIn()
                }
            } else {
                themePreferences.setSyncEnabled(false)
                syncScheduler.cancelPeriodicSync()
            }
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            result.onSuccess {
                themePreferences.setSyncEnabled(true)
                syncScheduler.schedulePeriodicSync()
                syncScheduler.requestImmediateSync()
                onSuccess()
            }.onFailure { error ->
                onError(error)
            }
        }
    }

    fun signOut(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            themePreferences.setSyncEnabled(false)
            syncScheduler.cancelPeriodicSync()
            themePreferences.setLastSyncTimestamp(0L)
            authRepository.signOut()
            onComplete()
        }
    }

    fun triggerManualSync() {
        syncScheduler.requestImmediateSync()
    }
}
