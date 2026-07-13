package com.pankaj.habitflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pankaj.habitflow.data.local.entity.HabitRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitRecordDao {

    // ── Queries ─────────────────────────────────────────────

    @Query("SELECT * FROM habit_records WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getRecord(habitId: String, date: String): HabitRecordEntity?

    @Query("SELECT habitId FROM habit_records WHERE date = :date AND isCompleted = 1 AND syncStatus != 'PENDING_DELETE'")
    fun getCompletedHabitIdsForDateFlow(date: String): Flow<List<String>>

    @Query("SELECT habitId FROM habit_records WHERE date = :date AND isCompleted = 1 AND syncStatus != 'PENDING_DELETE'")
    suspend fun getCompletedHabitIdsForDate(date: String): List<String>

    @Query("""
        SELECT COUNT(*) FROM habit_records 
        WHERE habitId = :habitId AND isCompleted = 1 AND syncStatus != 'PENDING_DELETE'
    """)
    suspend fun getTotalCompletions(habitId: String): Int

    @Query("""
        SELECT date FROM habit_records 
        WHERE habitId = :habitId AND isCompleted = 1 AND syncStatus != 'PENDING_DELETE'
        ORDER BY date DESC
    """)
    suspend fun getCompletedDatesForHabit(habitId: String): List<String>

    @Query("""
        SELECT * FROM habit_records 
        WHERE habitId = :habitId AND isCompleted = 1 AND syncStatus != 'PENDING_DELETE'
        ORDER BY date DESC
    """)
    fun getCompletedRecordsForHabitFlow(habitId: String): Flow<List<HabitRecordEntity>>

    @Query("""
        SELECT date FROM habit_records 
        WHERE habitId = :habitId AND isCompleted = 1 AND syncStatus != 'PENDING_DELETE'
        AND date >= :startDate AND date <= :endDate
        ORDER BY date ASC
    """)
    suspend fun getCompletedDatesForHabitInRange(
        habitId: String,
        startDate: String,
        endDate: String
    ): List<String>

    /**
     * Returns count of completed habits per date in a range.
     * Used for heat map calendar.
     */
    @Query("""
        SELECT date, COUNT(*) as count FROM habit_records 
        WHERE isCompleted = 1 AND syncStatus != 'PENDING_DELETE'
        AND date >= :startDate AND date <= :endDate
        GROUP BY date
    """)
    suspend fun getCompletionCountsByDate(
        startDate: String,
        endDate: String
    ): List<DateCount>

    @Query("""
        SELECT COUNT(DISTINCT habitId) FROM habit_records 
        WHERE date = :date AND isCompleted = 1 AND syncStatus != 'PENDING_DELETE'
    """)
    suspend fun getCompletedCountForDate(date: String): Int

    @Query("""
        SELECT * FROM habit_records 
        WHERE isCompleted = 1 AND syncStatus != 'PENDING_DELETE'
        AND date >= :startDate AND date <= :endDate
    """)
    suspend fun getCompletedRecordsInRange(
        startDate: String,
        endDate: String
    ): List<HabitRecordEntity>

    // ── Mutations ───────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: HabitRecordEntity)

    @Query("DELETE FROM habit_records WHERE habitId = :habitId AND date = :date")
    suspend fun deleteRecord(habitId: String, date: String)

    @Query("""
        UPDATE habit_records SET syncStatus = 'PENDING_DELETE', lastModified = :now 
        WHERE habitId = :habitId AND date = :date
    """)
    suspend fun softDeleteRecord(habitId: String, date: String, now: Long = System.currentTimeMillis())

    @Query("SELECT * FROM habit_records WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingRecords(): List<HabitRecordEntity>

    @Query("SELECT * FROM habit_records WHERE syncStatus != 'PENDING_DELETE'")
    suspend fun getAllRecords(): List<HabitRecordEntity>

    @Query("UPDATE habit_records SET syncStatus = 'SYNCED' WHERE id = :recordId")
    suspend fun markSynced(recordId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecord(record: HabitRecordEntity)

    @Query("DELETE FROM habit_records WHERE id = :recordId")
    suspend fun hardDeleteRecord(recordId: String)
}

/**
 * Helper data class for the heat map query result.
 */
data class DateCount(
    val date: String,
    val count: Int
)
