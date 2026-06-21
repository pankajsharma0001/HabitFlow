package com.pankaj.habitflow.domain.repository

import com.pankaj.habitflow.domain.model.DayStats
import com.pankaj.habitflow.domain.model.Habit
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for habits and their records.
 * This is the contract that the data layer must implement.
 */
interface HabitRepository {

    // ── Habit CRUD ──────────────────────────────────────────

    fun getAllHabitsFlow(): Flow<List<Habit>>

    fun getActiveHabitsFlow(): Flow<List<Habit>>

    fun getHabitByIdFlow(habitId: String): Flow<Habit?>

    suspend fun getHabitById(habitId: String): Habit?

    suspend fun insertHabit(
        name: String,
        description: String,
        category: String,
        colorHex: String,
        iconName: String,
        reminderTimeMinutes: Int?   // minutes from midnight, null = no reminder
    ): String                       // returns the new habit ID

    suspend fun updateHabit(
        habitId: String,
        name: String,
        description: String,
        category: String,
        colorHex: String,
        iconName: String,
        reminderTimeMinutes: Int?
    )

    suspend fun archiveHabit(habitId: String)

    suspend fun unarchiveHabit(habitId: String)

    suspend fun deleteHabit(habitId: String)

    // ── Habit Records (daily check-ins) ─────────────────────

    suspend fun toggleHabitCompletion(habitId: String, date: LocalDate)

    suspend fun isHabitCompletedOn(habitId: String, date: LocalDate): Boolean

    fun getCompletedHabitIdsForDateFlow(date: LocalDate): Flow<Set<String>>

    // ── Statistics ──────────────────────────────────────────

    suspend fun getDayStats(date: LocalDate): DayStats

    fun getDayStatsFlow(date: LocalDate): Flow<DayStats>

    suspend fun getStatsForRange(startDate: LocalDate, endDate: LocalDate): List<DayStats>

    suspend fun getCurrentStreak(habitId: String): Int

    suspend fun getLongestStreak(habitId: String): Int

    suspend fun getTotalCompletions(habitId: String): Int

    suspend fun getCompletionRate(habitId: String): Float

    /**
     * Returns a map of date -> completion count for the heat map calendar.
     */
    suspend fun getCompletionHeatMap(
        startDate: LocalDate,
        endDate: LocalDate
    ): Map<LocalDate, Int>

    /**
     * Returns the count of active (non-archived) habits.
     */
    suspend fun getActiveHabitCount(): Int
}
