package com.pankaj.habitflow.presentation.screen.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pankaj.habitflow.domain.model.Habit
import com.pankaj.habitflow.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    val habits: StateFlow<List<Habit>> = repository.getAllHabitsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun archiveHabit(habitId: String) {
        viewModelScope.launch {
            repository.archiveHabit(habitId)
        }
    }

    fun unarchiveHabit(habitId: String) {
        viewModelScope.launch {
            repository.unarchiveHabit(habitId)
        }
    }

    fun deleteHabit(habitId: String) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
        }
    }
}
