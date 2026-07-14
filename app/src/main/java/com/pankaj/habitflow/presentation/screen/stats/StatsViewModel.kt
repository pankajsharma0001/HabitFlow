package com.pankaj.habitflow.presentation.screen.stats

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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    private val _selectedRange = MutableStateFlow(StatsRange.WEEK)
    val selectedRange: StateFlow<StatsRange> = _selectedRange.asStateFlow()

    val habits: StateFlow<List<Habit>> = repository.getActiveHabitsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val chartStats: StateFlow<List<DayStats>> = _selectedRange
        .flatMapLatest { range ->
            val endDate = LocalDate.now()
            val startDate = when (range) {
                StatsRange.WEEK -> endDate.minusDays(6)
                StatsRange.MONTH -> endDate.minusDays(29)
            }
            repository.getActiveHabitsFlow().combine(repository.getAllHabitsFlow()) { _, _ ->
                repository.getStatsForRange(startDate, endDate)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _heatMapData = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val heatMapData: StateFlow<Map<LocalDate, Int>> = _heatMapData.asStateFlow()

    init {
        loadHeatMap()
    }

    fun setRange(range: StatsRange) {
        _selectedRange.value = range
    }

    fun loadHeatMap() {
        viewModelScope.launch {
            val endDate = LocalDate.now()
            val startDate = endDate.minusWeeks(16)
            val data = repository.getCompletionHeatMap(startDate, endDate)
            _heatMapData.value = data
        }
    }

    val budgetSpendStats: StateFlow<List<BudgetSpendStats>> = combine(
        repository.getActiveHabitsFlow(),
        repository.getAllCompletedRecordsFlow()
    ) { allHabits, allRecords ->
        val budgetHabits = allHabits.filter { it.habitType == "BUDGET" }
        val today = LocalDate.now()
        val startOfWeek = today.minusDays(6)
        val startOfMonth = today.minusDays(29)

        budgetHabits.map { habit ->
            val habitRecords = allRecords.filter { it.habitId == habit.id }
            val weeklyTotal = habitRecords
                .filter { !it.date.isBefore(startOfWeek) && !it.date.isAfter(today) }
                .sumOf { it.value ?: 0.0 }

            val monthlyTotal = habitRecords
                .filter { !it.date.isBefore(startOfMonth) && !it.date.isAfter(today) }
                .sumOf { it.value ?: 0.0 }

            val recentTransactions = habitRecords
                .filter { it.value != null }
                .sortedByDescending { it.date }
                .take(5)
                .map { record ->
                    SpendTransaction(
                        date = record.date,
                        amount = record.value ?: 0.0,
                        note = record.note
                    )
                }

            BudgetSpendStats(
                habitId = habit.id,
                habitName = habit.name,
                currencyUnit = habit.valueUnit ?: "$",
                weeklyTotal = weeklyTotal,
                monthlyTotal = monthlyTotal,
                recentTransactions = recentTransactions
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}

enum class StatsRange {
    WEEK, MONTH
}

data class SpendTransaction(
    val date: LocalDate,
    val amount: Double,
    val note: String?
)

data class BudgetSpendStats(
    val habitId: String,
    val habitName: String,
    val currencyUnit: String,
    val weeklyTotal: Double,
    val monthlyTotal: Double,
    val recentTransactions: List<SpendTransaction>
)
