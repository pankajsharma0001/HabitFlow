package com.pankaj.habitflow.domain.model

import java.time.LocalDate

/**
 * Represents statistics for a single day.
 */
data class DayStats(
    val date: LocalDate,
    val totalHabits: Int,
    val completedHabits: Int
) {
    val completionRate: Float
        get() = if (totalHabits > 0) completedHabits.toFloat() / totalHabits else 0f
}
