package com.momodding.backend.app.service.usercredential

import com.momodding.backend.app.dto.request.LoginRequest
import com.momodding.backend.app.dto.request.RegisterRequest
import com.momodding.backend.app.entity.UserCredential

interface UserCredentialService {

    fun findUserById(id: Long): UserCredential?

    fun findUserByEmail(email: String): UserCredential?

    fun saveOrUpdate(data: UserCredential): UserCredential

    fun updateLoginAttempt(data: UserCredential, attempt: Long): UserCredential

    fun updateStatus(data: UserCredential, userStatus: String): UserCredential

    fun doLogin(req: LoginRequest): Any

	fun doRegister(req: RegisterRequest) : Any
}