package com.pankaj.habitflow.presentation.screen.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pankaj.habitflow.domain.model.DayStats
import com.pankaj.habitflow.domain.model.Habit
import com.pankaj.habitflow.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TodayViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // Flow of habits for the selected date, incorporating active habits and archived habits completed on this date
    val habits: StateFlow<List<Habit>> = _selectedDate
        .flatMapLatest { date ->
            repository.getAllHabitsFlow().combine(
                repository.getCompletedHabitIdsForDateFlow(date)
            ) { allHabits, completedIds ->
                allHabits.filter { habit ->
                    val createdDate = java.time.Instant.ofEpochMilli(habit.createdAtMillis)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                    val wasCreated = !createdDate.isAfter(date)
                    val isApplicable = !habit.isArchived || (habit.id in completedIds)
                    wasCreated && isApplicable
                }.map { habit ->
                    habit.copy(isCompletedToday = habit.id in completedIds)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val dayStats: StateFlow<DayStats> = _selectedDate
        .flatMapLatest { date ->
            repository.getDayStatsFlow(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DayStats(LocalDate.now(), 0, 0)
        )

    fun toggleHabit(habitId: String) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habitId, _selectedDate.value)
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun moveToNextDay() {
        _selectedDate.value = _selectedDate.value.plusDays(1)
    }

    fun moveToPreviousDay() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
    }
}
