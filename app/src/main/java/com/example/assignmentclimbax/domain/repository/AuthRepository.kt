package com.example.assignmentclimbax.domain.repository

import com.example.assignmentclimbax.domain.model.UserSession

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<Int>
    suspend fun getLoggedInUserId(): Int?
    suspend fun getSession(): UserSession?
    suspend fun isLoggedIn(): Boolean
    suspend fun logout()
}
