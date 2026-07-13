package com.pankaj.habitflow.presentation.screen.habits

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pankaj.habitflow.domain.model.Habit
import com.pankaj.habitflow.domain.model.HabitCompletionRecord
import com.pankaj.habitflow.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    private val repository: HabitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val habitId: String = savedStateHandle.get<String>("habitId") ?: ""

    val habit: StateFlow<Habit?> = repository.getHabitByIdFlow(habitId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val history: StateFlow<List<HabitCompletionRecord>> = repository.getCompletionRecordsFlow(habitId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Map of date -> 1 (representing completion) for the heatmap calendar
    val heatMapData: StateFlow<Map<LocalDate, Int>> = history.map { records ->
        records.associate { it.date to 1 }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    fun updateNote(date: LocalDate, note: String) {
        viewModelScope.launch {
            repository.updateCompletionNote(habitId, date, note.trim())
        }
    }
}
