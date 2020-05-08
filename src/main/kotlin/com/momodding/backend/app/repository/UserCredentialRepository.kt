package com.momodding.backend.app.repository

import com.momodding.backend.app.entity.UserCredential
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserCredentialRepository : JpaRepository<UserCredential, Long> {
	abstract fun findByEmail(email: String): Optional<UserCredential>?
}