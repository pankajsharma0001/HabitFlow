package com.pankaj.habitflow.domain.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserFlow: Flow<FirebaseUser?>
    val currentUser: FirebaseUser?
    val currentUserId: String?
    fun isSignedIn(): Boolean
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser>
    suspend fun signOut(): Result<Unit>
}
