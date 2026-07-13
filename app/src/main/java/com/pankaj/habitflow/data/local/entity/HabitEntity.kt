package com.pankaj.habitflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pankaj.habitflow.domain.model.SyncStatus
import java.util.UUID

/**
 * Room entity representing a habit definition.
 */
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val category: String,
    val colorHex: String,
    val iconName: String = "check_circle",
    val reminderTimeMinutes: Int? = null,   // minutes from midnight (e.g., 480 = 8:00 AM)
    val createdAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false,
    val frequencyType: String = "DAILY",
    val frequencyDays: String? = null,
    val sortOrder: Int = 0,
    val timeOfDay: String = "ANYTIME",
    val syncStatus: String = SyncStatus.PENDING_INSERT.name,
    val lastModified: Long = System.currentTimeMillis()
)
