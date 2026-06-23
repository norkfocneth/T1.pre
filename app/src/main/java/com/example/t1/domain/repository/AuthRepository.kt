package com.example.t1.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isSignedIn: Flow<Boolean>
    val currentUserId: Flow<String?>

    suspend fun signUpWithEmail(email: String, password: String): Result<String>
    suspend fun signInWithEmail(email: String, password: String): Result<String>
    suspend fun signOut(): Result<Unit>
}
