package com.pankaj.habitflow.domain.model

/**
 * Sync status for offline-first architecture.
 * Tracks whether local changes have been pushed to the cloud.
 */
enum class SyncStatus {
    SYNCED,
    PENDING_INSERT,
    PENDING_UPDATE,
    PENDING_DELETE
}
