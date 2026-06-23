package com.pankaj.habitflow.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pankaj.habitflow.data.local.ThemePreferences
import com.pankaj.habitflow.domain.repository.AuthRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val authRepository: AuthRepository,
    private val firestoreSync: FirestoreSync,
    private val preferences: ThemePreferences
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val syncEnabled = preferences.syncEnabledFlow.first()
        if (!syncEnabled) {
            return Result.success()
        }

        val uid = authRepository.currentUserId
        if (uid == null) {
            preferences.setSyncEnabled(false)
            return Result.success()
        }

        return try {
            val syncResult = firestoreSync.sync(uid)
            if (syncResult.isSuccess) {
                Result.success()
            } else {
                if (runAttemptCount < 3) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
