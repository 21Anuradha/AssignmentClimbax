package com.example.assignmentclimbax.data.repository

import com.example.assignmentclimbax.data.local.dao.CartDao
import com.example.assignmentclimbax.data.prefs.SessionDataStore
import com.example.assignmentclimbax.data.remote.NetworkErrorMapper
import com.example.assignmentclimbax.data.remote.api.DummyJsonApi
import com.example.assignmentclimbax.data.remote.dto.LoginRequest
import com.example.assignmentclimbax.domain.model.UserSession
import com.example.assignmentclimbax.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: DummyJsonApi,
    private val sessionDataStore: SessionDataStore,
    private val cartDao: CartDao
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<Int> = try {
        val response = api.login(LoginRequest(username.trim(), password))
        if (response.id <= 0) {
            Result.failure(Exception("Invalid login response from server."))
        } else {
            sessionDataStore.saveSession(
                id = response.id,
                email = response.email.orEmpty(),
                firstName = response.firstName.orEmpty(),
                lastName = response.lastName.orEmpty(),
                image = response.image.orEmpty()
            )
            Result.success(response.id)
        }
    } catch (e: Exception) {
        Result.failure(Exception(NetworkErrorMapper.map(e), e))
    }

    override suspend fun getLoggedInUserId(): Int? = sessionDataStore.getSession()?.id

    override suspend fun getSession(): UserSession? = sessionDataStore.getSession()

    override suspend fun isLoggedIn(): Boolean = sessionDataStore.isLoggedIn()

    override suspend fun logout() {
        sessionDataStore.clear()
        cartDao.clearAll()
    }
}
