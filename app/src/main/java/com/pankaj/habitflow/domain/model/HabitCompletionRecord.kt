package com.pankaj.habitflow.domain.model

import java.time.LocalDate

data class HabitCompletionRecord(
    val id: String,
    val habitId: String,
    val date: LocalDate,
    val completedAtMillis: Long?,
    val note: String?,
    val value: Double? = null
)
