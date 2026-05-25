package com.example.assignmentclimbax.domain.model

data class UserSession(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val image: String
) {
    val fullName: String
        get() = listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { email }
}
