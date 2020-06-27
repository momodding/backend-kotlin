package com.momodding.backend.app.service.usertoken

import com.momodding.backend.app.dto.response.RefreshAuthResponse

interface UserTokenService {
    fun doGenerateNewToken(refreshToken: String): RefreshAuthResponse
}