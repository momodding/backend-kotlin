package com.momodding.backend.app.repository

import com.momodding.backend.app.entity.UserToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserTokenRepository : JpaRepository<UserToken, Long> {
    abstract fun findByRefreshTokenAndExpiredInGreaterThanOrderByCreatedAtDesc(refreshToken: String, expiredId: Date): Optional<UserToken>?
}