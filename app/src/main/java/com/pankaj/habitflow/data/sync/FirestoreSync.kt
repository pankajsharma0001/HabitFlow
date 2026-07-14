package com.pankaj.habitflow.data.sync

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.pankaj.habitflow.data.local.ThemePreferences
import com.pankaj.habitflow.data.local.dao.HabitDao
import com.pankaj.habitflow.data.local.dao.HabitRecordDao
import com.pankaj.habitflow.data.local.entity.HabitEntity
import com.pankaj.habitflow.data.local.entity.HabitRecordEntity
import com.pankaj.habitflow.domain.model.SyncStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FirestoreSync @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val habitDao: HabitDao,
    private val habitRecordDao: HabitRecordDao,
    private val preferences: ThemePreferences
) {

    suspend fun sync(uid: String): Result<Unit> {
        return try {
            val lastSyncTimestamp = preferences.lastSyncTimestampFlow.first()
            val currentSyncTime = System.currentTimeMillis()

            // 1. Push local changes to Firestore
            pushLocalChanges(uid)

            // 2. Pull remote changes from Firestore
            pullRemoteChanges(uid, lastSyncTimestamp)

            // 3. Save new sync timestamp
            preferences.setLastSyncTimestamp(currentSyncTime)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun pushLocalChanges(uid: String) {
        // Push Habits
        val pendingHabits = habitDao.getPendingHabits()
        for (habit in pendingHabits) {
            val docRef = firestore.collection("users").document(uid)
                .collection("habits").document(habit.id)

            if (habit.syncStatus == SyncStatus.PENDING_DELETE.name) {
                // Soft delete on Firestore
                val data = mapOf(
                    "isDeleted" to true,
                    "lastModified" to habit.lastModified
                )
                docRef.set(data).await()
                habitDao.hardDeleteHabit(habit.id)
            } else {
                // Update/Insert on Firestore
                val data = mapOf(
                    "id" to habit.id,
                    "name" to habit.name,
                    "description" to habit.description,
                    "category" to habit.category,
                    "colorHex" to habit.colorHex,
                    "iconName" to habit.iconName,
                    "reminderTimeMinutes" to habit.reminderTimeMinutes,
                    "createdAt" to habit.createdAt,
                    "isArchived" to habit.isArchived,
                    "frequencyType" to habit.frequencyType,
                    "frequencyDays" to habit.frequencyDays,
                    "habitType" to habit.habitType,
                    "targetValue" to habit.targetValue,
                    "valueUnit" to habit.valueUnit,
                    "isDeleted" to false,
                    "lastModified" to habit.lastModified
                )
                docRef.set(data).await()
                habitDao.markSynced(habit.id)
            }
        }

        // Push Records
        val pendingRecords = habitRecordDao.getPendingRecords()
        for (record in pendingRecords) {
            val docRef = firestore.collection("users").document(uid)
                .collection("records").document(record.id)

            if (record.syncStatus == SyncStatus.PENDING_DELETE.name) {
                val data = mapOf(
                    "isDeleted" to true,
                    "lastModified" to record.lastModified
                )
                docRef.set(data).await()
                habitRecordDao.hardDeleteRecord(record.id)
            } else {
                val data = mapOf(
                    "id" to record.id,
                    "habitId" to record.habitId,
                    "date" to record.date,
                    "isCompleted" to record.isCompleted,
                    "completedAt" to record.completedAt,
                    "value" to record.value,
                    "note" to record.note,
                    "isDeleted" to false,
                    "lastModified" to record.lastModified
                )
                docRef.set(data).await()
                habitRecordDao.markSynced(record.id)
            }
        }
    }

    private suspend fun pullRemoteChanges(uid: String, lastSyncTimestamp: Long) {
        // Pull Habits
        val habitsQuery = firestore.collection("users").document(uid)
            .collection("habits")
            .whereGreaterThan("lastModified", lastSyncTimestamp)
            .get()
            .await()

        for (doc in habitsQuery.documents) {
            val id = doc.id
            val isDeleted = doc.getBoolean("isDeleted") ?: false
            val lastModified = doc.getLong("lastModified") ?: 0L

            if (isDeleted) {
                habitDao.hardDeleteHabit(id)
            } else {
                val localHabit = habitDao.getHabitById(id)
                if (localHabit == null || lastModified > localHabit.lastModified) {
                    val habit = HabitEntity(
                        id = id,
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        category = doc.getString("category") ?: "",
                        colorHex = doc.getString("colorHex") ?: "#FF4688F1",
                        iconName = doc.getString("iconName") ?: "check_circle",
                        reminderTimeMinutes = doc.getLong("reminderTimeMinutes")?.toInt(),
                        createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                        isArchived = doc.getBoolean("isArchived") ?: false,
                        frequencyType = doc.getString("frequencyType") ?: "DAILY",
                        frequencyDays = doc.getString("frequencyDays"),
                        habitType = doc.getString("habitType") ?: "NORMAL",
                        targetValue = doc.getDouble("targetValue"),
                        valueUnit = doc.getString("valueUnit"),
                        syncStatus = SyncStatus.SYNCED.name,
                        lastModified = lastModified
                    )
                    habitDao.upsertHabit(habit)
                }
            }
        }

        // Pull Records
        val recordsQuery = firestore.collection("users").document(uid)
            .collection("records")
            .whereGreaterThan("lastModified", lastSyncTimestamp)
            .get()
            .await()

        for (doc in recordsQuery.documents) {
            val id = doc.id
            val isDeleted = doc.getBoolean("isDeleted") ?: false
            val lastModified = doc.getLong("lastModified") ?: 0L

            if (isDeleted) {
                habitRecordDao.hardDeleteRecord(id)
            } else {
                val habitId = doc.getString("habitId") ?: continue
                // First check if the habit exists in the local DB. Because of foreign key constraint,
                // we can't insert a record if the corresponding habit doesn't exist locally!
                if (habitDao.getHabitById(habitId) == null) {
                    // Try to fetch this specific habit from Firestore if it is missing locally
                    val habitDoc = firestore.collection("users").document(uid)
                        .collection("habits").document(habitId).get().await()
                    if (habitDoc.exists() && !(habitDoc.getBoolean("isDeleted") ?: false)) {
                        val habit = HabitEntity(
                            id = habitId,
                            name = habitDoc.getString("name") ?: "",
                            description = habitDoc.getString("description") ?: "",
                            category = habitDoc.getString("category") ?: "",
                            colorHex = habitDoc.getString("colorHex") ?: "#FF4688F1",
                            iconName = habitDoc.getString("iconName") ?: "check_circle",
                            reminderTimeMinutes = habitDoc.getLong("reminderTimeMinutes")?.toInt(),
                            createdAt = habitDoc.getLong("createdAt") ?: System.currentTimeMillis(),
                            isArchived = habitDoc.getBoolean("isArchived") ?: false,
                            frequencyType = habitDoc.getString("frequencyType") ?: "DAILY",
                            frequencyDays = habitDoc.getString("frequencyDays"),
                            habitType = habitDoc.getString("habitType") ?: "NORMAL",
                            targetValue = habitDoc.getDouble("targetValue"),
                            valueUnit = habitDoc.getString("valueUnit"),
                            syncStatus = SyncStatus.SYNCED.name,
                            lastModified = habitDoc.getLong("lastModified") ?: 0L
                        )
                        habitDao.upsertHabit(habit)
                    } else {
                        // Skip this record if its parent habit is deleted or missing
                        continue
                    }
                }

                // Check if the record already exists locally
                val date = doc.getString("date") ?: continue
                val localRecord = habitRecordDao.getRecord(habitId, date)
                if (localRecord == null || lastModified > localRecord.lastModified) {
                    val record = HabitRecordEntity(
                        id = id,
                        habitId = habitId,
                        date = date,
                        isCompleted = doc.getBoolean("isCompleted") ?: true,
                        completedAt = doc.getLong("completedAt"),
                        value = doc.getDouble("value"),
                        note = doc.getString("note"),
                        syncStatus = SyncStatus.SYNCED.name,
                        lastModified = lastModified
                    )
                    habitRecordDao.upsertRecord(record)
                }
            }
        }
    }

    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result)
            } else {
                continuation.resumeWithException(task.exception ?: RuntimeException("Firestore Task failed"))
            }
        }
    }
}
