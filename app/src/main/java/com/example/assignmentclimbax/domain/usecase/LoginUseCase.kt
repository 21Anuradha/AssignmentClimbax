package com.example.assignmentclimbax.domain.usecase

import com.example.assignmentclimbax.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(username: String, password: String): Result<Int> =
        authRepository.login(username, password)
}
