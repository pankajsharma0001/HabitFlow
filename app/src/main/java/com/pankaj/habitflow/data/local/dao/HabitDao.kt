package com.pankaj.habitflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pankaj.habitflow.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    // ── Queries ─────────────────────────────────────────────

    @Query("SELECT * FROM habits WHERE syncStatus != 'PENDING_DELETE' ORDER BY createdAt DESC")
    fun getAllHabitsFlow(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE isArchived = 0 AND syncStatus != 'PENDING_DELETE' ORDER BY createdAt DESC")
    fun getActiveHabitsFlow(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :habitId AND syncStatus != 'PENDING_DELETE'")
    fun getHabitByIdFlow(habitId: String): Flow<HabitEntity?>

    @Query("SELECT * FROM habits WHERE id = :habitId AND syncStatus != 'PENDING_DELETE'")
    suspend fun getHabitById(habitId: String): HabitEntity?

    @Query("SELECT * FROM habits WHERE isArchived = 0 AND syncStatus != 'PENDING_DELETE'")
    suspend fun getActiveHabits(): List<HabitEntity>

    @Query("SELECT * FROM habits WHERE syncStatus != 'PENDING_DELETE'")
    suspend fun getAllHabits(): List<HabitEntity>

    @Query("SELECT COUNT(*) FROM habits WHERE isArchived = 0 AND syncStatus != 'PENDING_DELETE'")
    suspend fun getActiveHabitCount(): Int

    @Query("SELECT * FROM habits WHERE reminderTimeMinutes IS NOT NULL AND isArchived = 0 AND syncStatus != 'PENDING_DELETE'")
    suspend fun getHabitsWithReminders(): List<HabitEntity>

    // ── Mutations ───────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("UPDATE habits SET isArchived = 1, lastModified = :now, syncStatus = 'PENDING_UPDATE' WHERE id = :habitId")
    suspend fun archiveHabit(habitId: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE habits SET isArchived = 0, lastModified = :now, syncStatus = 'PENDING_UPDATE' WHERE id = :habitId")
    suspend fun unarchiveHabit(habitId: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE habits SET syncStatus = 'PENDING_DELETE', lastModified = :now WHERE id = :habitId")
    suspend fun softDeleteHabit(habitId: String, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun hardDeleteHabit(habitId: String)
}
