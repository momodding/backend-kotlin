package com.momodding.backend.app.dto.response

data class RefreshAuthResponse(
        val accessToken: String?,
        val refreshToken: String?
)