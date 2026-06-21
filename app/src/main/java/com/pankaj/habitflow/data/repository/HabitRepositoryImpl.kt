package com.pankaj.habitflow.data.repository

import com.pankaj.habitflow.data.local.dao.HabitDao
import com.pankaj.habitflow.data.local.dao.HabitRecordDao
import com.pankaj.habitflow.data.local.entity.HabitEntity
import com.pankaj.habitflow.data.local.entity.HabitRecordEntity
import com.pankaj.habitflow.domain.model.DayStats
import com.pankaj.habitflow.domain.model.Habit
import com.pankaj.habitflow.domain.model.HabitCategory
import com.pankaj.habitflow.domain.model.SyncStatus
import com.pankaj.habitflow.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import com.pankaj.habitflow.notification.AlarmScheduler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val recordDao: HabitRecordDao,
    private val alarmScheduler: AlarmScheduler
) : HabitRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE  // "yyyy-MM-dd"

    // ── Habit CRUD ──────────────────────────────────────────

    override fun getAllHabitsFlow(): Flow<List<Habit>> {
        val today = LocalDate.now().format(dateFormatter)
        return combine(
            habitDao.getAllHabitsFlow(),
            recordDao.getCompletedHabitIdsForDateFlow(today)
        ) { habits, completedIds ->
            habits.map { entity -> mapToHabit(entity, completedIds.toSet()) }
        }
    }

    override fun getActiveHabitsFlow(): Flow<List<Habit>> {
        val today = LocalDate.now().format(dateFormatter)
        return combine(
            habitDao.getActiveHabitsFlow(),
            recordDao.getCompletedHabitIdsForDateFlow(today)
        ) { habits, completedIds ->
            habits.map { entity -> mapToHabit(entity, completedIds.toSet()) }
        }
    }

    override fun getHabitByIdFlow(habitId: String): Flow<Habit?> {
        val today = LocalDate.now().format(dateFormatter)
        return combine(
            habitDao.getHabitByIdFlow(habitId),
            recordDao.getCompletedHabitIdsForDateFlow(today)
        ) { entity, completedIds ->
            entity?.let { mapToHabit(it, completedIds.toSet()) }
        }
    }

    override suspend fun getHabitById(habitId: String): Habit? {
        val entity = habitDao.getHabitById(habitId) ?: return null
        val today = LocalDate.now().format(dateFormatter)
        val completedIds = recordDao.getCompletedHabitIdsForDate(today).toSet()
        return mapToHabit(entity, completedIds)
    }

    override suspend fun insertHabit(
        name: String,
        description: String,
        category: String,
        colorHex: String,
        iconName: String,
        reminderTimeMinutes: Int?
    ): String {
        val id = UUID.randomUUID().toString()
        val entity = HabitEntity(
            id = id,
            name = name,
            description = description,
            category = category,
            colorHex = colorHex,
            iconName = iconName,
            reminderTimeMinutes = reminderTimeMinutes,
            createdAt = System.currentTimeMillis(),
            syncStatus = SyncStatus.PENDING_INSERT.name
        )
        habitDao.insertHabit(entity)
        
        if (reminderTimeMinutes != null) {
            alarmScheduler.scheduleAlarm(id, name, description, reminderTimeMinutes)
        }
        
        return id
    }

    override suspend fun updateHabit(
        habitId: String,
        name: String,
        description: String,
        category: String,
        colorHex: String,
        iconName: String,
        reminderTimeMinutes: Int?
    ) {
        val existing = habitDao.getHabitById(habitId) ?: return
        val updated = existing.copy(
            name = name,
            description = description,
            category = category,
            colorHex = colorHex,
            iconName = iconName,
            reminderTimeMinutes = reminderTimeMinutes,
            lastModified = System.currentTimeMillis(),
            syncStatus = if (existing.syncStatus == SyncStatus.PENDING_INSERT.name)
                SyncStatus.PENDING_INSERT.name
            else SyncStatus.PENDING_UPDATE.name
        )
        habitDao.updateHabit(updated)

        alarmScheduler.cancelAlarm(habitId)
        if (!existing.isArchived && reminderTimeMinutes != null) {
            alarmScheduler.scheduleAlarm(habitId, name, description, reminderTimeMinutes)
        }
    }

    override suspend fun archiveHabit(habitId: String) {
        habitDao.archiveHabit(habitId)
        alarmScheduler.cancelAlarm(habitId)
    }

    override suspend fun unarchiveHabit(habitId: String) {
        habitDao.unarchiveHabit(habitId)
        val habit = habitDao.getHabitById(habitId)
        if (habit != null && habit.reminderTimeMinutes != null) {
            alarmScheduler.scheduleAlarm(
                habit.id,
                habit.name,
                habit.description,
                habit.reminderTimeMinutes
            )
        }
    }

    override suspend fun deleteHabit(habitId: String) {
        habitDao.hardDeleteHabit(habitId)
        alarmScheduler.cancelAlarm(habitId)
    }

    // ── Habit Records ───────────────────────────────────────

    override suspend fun toggleHabitCompletion(habitId: String, date: LocalDate) {
        val dateStr = date.format(dateFormatter)
        val existing = recordDao.getRecord(habitId, dateStr)
        if (existing != null) {
            // Toggle: if completed, remove; if not completed, mark completed
            if (existing.isCompleted) {
                recordDao.deleteRecord(habitId, dateStr)
            } else {
                recordDao.insertRecord(
                    existing.copy(
                        isCompleted = true,
                        completedAt = System.currentTimeMillis(),
                        lastModified = System.currentTimeMillis(),
                        syncStatus = SyncStatus.PENDING_UPDATE.name
                    )
                )
            }
        } else {
            // Create new completion record
            recordDao.insertRecord(
                HabitRecordEntity(
                    id = UUID.randomUUID().toString(),
                    habitId = habitId,
                    date = dateStr,
                    isCompleted = true,
                    completedAt = System.currentTimeMillis(),
                    syncStatus = SyncStatus.PENDING_INSERT.name
                )
            )
        }
    }

    override suspend fun isHabitCompletedOn(habitId: String, date: LocalDate): Boolean {
        val dateStr = date.format(dateFormatter)
        val record = recordDao.getRecord(habitId, dateStr)
        return record?.isCompleted == true
    }

    override fun getCompletedHabitIdsForDateFlow(date: LocalDate): Flow<Set<String>> {
        val dateStr = date.format(dateFormatter)
        return recordDao.getCompletedHabitIdsForDateFlow(dateStr).map { it.toSet() }
    }

    // ── Statistics ──────────────────────────────────────────

    override suspend fun getDayStats(date: LocalDate): DayStats {
        val dateStr = date.format(dateFormatter)
        val allHabits = habitDao.getAllHabits()
        val completedIds = recordDao.getCompletedHabitIdsForDate(dateStr).toSet()
        val total = allHabits.count { entity ->
            val createdDate = java.time.Instant.ofEpochMilli(entity.createdAt)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
            val wasCreated = !createdDate.isAfter(date)
            val isApplicable = !entity.isArchived || (entity.id in completedIds)
            wasCreated && isApplicable
        }
        return DayStats(
            date = date,
            totalHabits = total,
            completedHabits = completedIds.size
        )
    }

    override fun getDayStatsFlow(date: LocalDate): Flow<DayStats> {
        val dateStr = date.format(dateFormatter)
        return combine(
            habitDao.getAllHabitsFlow(),
            recordDao.getCompletedHabitIdsForDateFlow(dateStr)
        ) { entities, completedIds ->
            val total = entities.count { entity ->
                val createdDate = java.time.Instant.ofEpochMilli(entity.createdAt)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                val wasCreated = !createdDate.isAfter(date)
                val isApplicable = !entity.isArchived || (entity.id in completedIds)
                wasCreated && isApplicable
            }
            DayStats(
                date = date,
                totalHabits = total,
                completedHabits = completedIds.size
            )
        }
    }

    override suspend fun getStatsForRange(startDate: LocalDate, endDate: LocalDate): List<DayStats> {
        val allHabits = habitDao.getAllHabits()
        val records = recordDao.getCompletedRecordsInRange(
            startDate.format(dateFormatter),
            endDate.format(dateFormatter)
        )
        val completedMap = records.groupBy { it.date }.mapValues { entry ->
            entry.value.map { it.habitId }.toSet()
        }

        val result = mutableListOf<DayStats>()
        var current = startDate
        while (!current.isAfter(endDate)) {
            val dateStr = current.format(dateFormatter)
            val completedIds = completedMap[dateStr] ?: emptySet()
            
            val total = allHabits.count { entity ->
                val createdDate = java.time.Instant.ofEpochMilli(entity.createdAt)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                val wasCreated = !createdDate.isAfter(current)
                val isApplicable = !entity.isArchived || (entity.id in completedIds)
                wasCreated && isApplicable
            }

            result.add(
                DayStats(
                    date = current,
                    totalHabits = total,
                    completedHabits = completedIds.size
                )
            )
            current = current.plusDays(1)
        }
        return result
    }

    override suspend fun getCurrentStreak(habitId: String): Int {
        val completedDates = recordDao.getCompletedDatesForHabit(habitId)
            .map { LocalDate.parse(it, dateFormatter) }
            .sortedDescending()

        if (completedDates.isEmpty()) return 0

        var streak = 0
        var expectedDate = LocalDate.now()

        // If today is not completed, start checking from yesterday
        if (completedDates.first() != expectedDate) {
            expectedDate = expectedDate.minusDays(1)
        }

        for (date in completedDates) {
            if (date == expectedDate) {
                streak++
                expectedDate = expectedDate.minusDays(1)
            } else if (date.isBefore(expectedDate)) {
                break
            }
        }
        return streak
    }

    override suspend fun getLongestStreak(habitId: String): Int {
        val completedDates = recordDao.getCompletedDatesForHabit(habitId)
            .map { LocalDate.parse(it, dateFormatter) }
            .sorted()

        if (completedDates.isEmpty()) return 0

        var longest = 1
        var current = 1

        for (i in 1 until completedDates.size) {
            if (completedDates[i] == completedDates[i - 1].plusDays(1)) {
                current++
                longest = maxOf(longest, current)
            } else if (completedDates[i] != completedDates[i - 1]) {
                current = 1
            }
        }
        return longest
    }

    override suspend fun getTotalCompletions(habitId: String): Int {
        return recordDao.getTotalCompletions(habitId)
    }

    override suspend fun getCompletionRate(habitId: String): Float {
        val entity = habitDao.getHabitById(habitId) ?: return 0f
        val createdDate = java.time.Instant.ofEpochMilli(entity.createdAt)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
        val today = LocalDate.now()
        val totalDays = java.time.temporal.ChronoUnit.DAYS.between(createdDate, today) + 1
        if (totalDays <= 0) return 0f

        val completions = recordDao.getTotalCompletions(habitId)
        return (completions.toFloat() / totalDays).coerceIn(0f, 1f)
    }

    override suspend fun getCompletionHeatMap(
        startDate: LocalDate,
        endDate: LocalDate
    ): Map<LocalDate, Int> {
        return recordDao.getCompletionCountsByDate(
            startDate.format(dateFormatter),
            endDate.format(dateFormatter)
        ).associate { LocalDate.parse(it.date, dateFormatter) to it.count }
    }

    override suspend fun getActiveHabitCount(): Int {
        return habitDao.getActiveHabitCount()
    }

    // ── Mapping ─────────────────────────────────────────────

    private suspend fun mapToHabit(entity: HabitEntity, completedTodayIds: Set<String>): Habit {
        val currentStreak = getCurrentStreak(entity.id)
        val longestStreak = getLongestStreak(entity.id)
        val totalCompletions = getTotalCompletions(entity.id)
        val completionRate = getCompletionRate(entity.id)

        return Habit(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            category = HabitCategory.fromName(entity.category),
            colorHex = entity.colorHex,
            iconName = entity.iconName,
            reminderTime = entity.reminderTimeMinutes?.let {
                LocalTime.of(it / 60, it % 60)
            },
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalCompletions = totalCompletions,
            completionRate = completionRate,
            isCompletedToday = entity.id in completedTodayIds,
            isArchived = entity.isArchived,
            createdAtMillis = entity.createdAt
        )
    }
}
