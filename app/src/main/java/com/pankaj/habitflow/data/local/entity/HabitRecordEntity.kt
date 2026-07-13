package com.pankaj.habitflow.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pankaj.habitflow.domain.model.SyncStatus
import java.util.UUID

/**
 * Room entity representing a daily habit completion record.
 * A record exists for each (habit, date) pair where the user toggled the habit.
 */
@Entity(
    tableName = "habit_records",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habitId"]),
        Index(value = ["date"]),
        Index(value = ["habitId", "date"], unique = true)
    ]
)
data class HabitRecordEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val habitId: String,
    val date: String,               // ISO format "yyyy-MM-dd"
    val isCompleted: Boolean = true,
    val completedAt: Long? = null,
    val note: String? = null,
    val syncStatus: String = SyncStatus.PENDING_INSERT.name,
    val lastModified: Long = System.currentTimeMillis()
)
