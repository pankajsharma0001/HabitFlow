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
            val startOfWeek = date.minusDays((date.dayOfWeek.value - 1).toLong())
            val endOfWeek = startOfWeek.plusDays(6)

            combine(
                repository.getAllHabitsFlow(),
                repository.getRecordsForDateFlow(date),
                repository.getCompletedRecordsInRangeFlow(startOfWeek, endOfWeek)
            ) { allHabits, todayRecords, weeklyRecords ->
                val todayRecordMap = todayRecords.associateBy { it.habitId }
                val weeklyRecordMap = weeklyRecords.groupBy { it.habitId }

                allHabits.filter { habit ->
                    val createdDate = java.time.Instant.ofEpochMilli(habit.createdAtMillis)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                    val wasCreated = !createdDate.isAfter(date)
                    if (!wasCreated) return@filter false

                    val isCompletedToday = todayRecordMap[habit.id]?.completedAtMillis != null

                    // If archived, only show if it was completed today
                    if (habit.isArchived) {
                        return@filter isCompletedToday
                    }

                    // Frequency day-filtering rules
                    when (habit.frequencyType) {
                        "DAILY" -> true
                        "CUSTOM_DAYS" -> {
                            // Show only on the days of the week in which it is set OR if completed today
                            val dayOfWeek = date.dayOfWeek.value // 1 to 7
                            dayOfWeek in habit.frequencyDays || isCompletedToday
                        }
                        "FLEXIBLE_DAYS" -> {
                            // Show on all days of the week until completed target completions, then only show on completed days
                            val targetCompletions = habit.frequencyDays.firstOrNull() ?: 3
                            val completionsInWeek = weeklyRecordMap[habit.id]?.map { it.date }?.distinct() ?: emptyList()
                            val completionsCount = completionsInWeek.size
                            
                            completionsCount < targetCompletions || isCompletedToday
                        }
                        else -> true
                    }
                }.map { habit ->
                    val record = todayRecordMap[habit.id]
                    habit.copy(
                        isCompletedToday = record?.completedAtMillis != null,
                        valueToday = record?.value,
                        noteToday = record?.note
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val dayStats: StateFlow<DayStats> = habits
        .map { habitList ->
            val total = habitList.size
            val completed = habitList.count { it.isCompletedToday }
            DayStats(
                date = _selectedDate.value,
                totalHabits = total,
                completedHabits = completed
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DayStats(LocalDate.now(), 0, 0)
        )

    fun toggleHabit(habitId: String) {
        viewModelScope.launch {
            val habit = habits.value.find { it.id == habitId } ?: return@launch
            if (habit.habitType == "QUANTITY") {
                if (habit.isCompletedToday) {
                    repository.logHabitProgress(habitId, _selectedDate.value, 0.0, false, null)
                } else {
                    val target = habit.targetValue ?: 1.0
                    repository.logHabitProgress(habitId, _selectedDate.value, target, true, null)
                }
            } else if (habit.habitType == "BUDGET") {
                if (habit.isCompletedToday) {
                    repository.logHabitProgress(habitId, _selectedDate.value, null, false, null)
                } else {
                    repository.logHabitProgress(habitId, _selectedDate.value, 0.0, true, "Quick log")
                }
            } else {
                repository.toggleHabitCompletion(habitId, _selectedDate.value)
            }
        }
    }

    fun logProgress(habitId: String, value: Double?, isCompleted: Boolean, note: String? = null) {
        viewModelScope.launch {
            repository.logHabitProgress(habitId, _selectedDate.value, value, isCompleted, note)
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun moveToNextDay() {
        val nextDate = _selectedDate.value.plusDays(1)
        if (!nextDate.isAfter(LocalDate.now())) {
            _selectedDate.value = nextDate
        }
    }

    fun moveToPreviousDay() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
    }
}
