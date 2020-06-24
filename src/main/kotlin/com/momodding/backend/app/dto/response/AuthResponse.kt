package com.momodding.backend.app.dto.response

data class AuthResponse(
        val accessToken: String?,
        val username: String?,
        val email: String?,
        val role: Long?
)