package com.momodding.backend.app.service.usertoken

import com.momodding.backend.app.dto.response.RefreshAuthResponse
import com.momodding.backend.app.repository.UserCredentialRepository
import com.momodding.backend.app.repository.UserTokenRepository
import com.momodding.backend.config.auth.JwtUtils
import com.momodding.backend.config.auth.TokenPayload
import com.momodding.backend.exception.DataNotFoundException
import com.momodding.backend.exception.UnauthorizedException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserTokenServiceImpl @Autowired constructor(
        val userTokenRepository: UserTokenRepository,
        val userCredentialRepository: UserCredentialRepository,
        val jwtUtils: JwtUtils
) : UserTokenService {

    override fun doGenerateNewToken(refreshToken: String): RefreshAuthResponse {
        val findRefreshToken = userTokenRepository.findByRefreshTokenAndExpiredInGreaterThanOrderByCreatedAtDesc(refreshToken, Date())

        when (findRefreshToken) {
            null -> throw UnauthorizedException("token expired")
            else -> {
                when (val user = userCredentialRepository.findById(findRefreshToken.get().ucId!!)) {
                    null -> throw DataNotFoundException("user not found")
                    else -> {
                        val tokenPayload = TokenPayload(
                                ucId = user.get().id!!,
                                email = user.get().email ?: "",
                                userRole = user.get().role,
                                issuedAt = Date().time
                        )

                        return RefreshAuthResponse(
                                accessToken = jwtUtils.generateToken(tokenPayload),
                                refreshToken = refreshToken
                        )
                    }
                }
            }
        }
    }
}