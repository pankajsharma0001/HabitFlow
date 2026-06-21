package com.pankaj.habitflow.domain.model

import java.time.LocalTime

/**
 * Domain model representing a Habit.
 * This is the clean model used by the UI and business logic.
 */
data class Habit(
    val id: String,
    val name: String,
    val description: String,
    val category: HabitCategory,
    val colorHex: String,
    val iconName: String,
    val reminderTime: LocalTime?,
    val currentStreak: Int,
    val longestStreak: Int,
    val totalCompletions: Int,
    val completionRate: Float,
    val isCompletedToday: Boolean,
    val isArchived: Boolean,
    val createdAtMillis: Long
)
