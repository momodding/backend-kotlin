package com.momodding.backend.app.service.usercredential

import com.momodding.backend.app.dto.request.LoginRequest
import com.momodding.backend.app.dto.request.RegisterRequest
import com.momodding.backend.app.dto.response.AuthResponse
import com.momodding.backend.app.entity.UserCredential
import com.momodding.backend.app.entity.UserToken
import com.momodding.backend.app.repository.UserCredentialRepository
import com.momodding.backend.app.repository.UserTokenRepository
import com.momodding.backend.config.auth.JwtUtils
import com.momodding.backend.config.auth.TokenPayload
import com.momodding.backend.exception.AppException
import com.momodding.backend.exception.DataNotFoundException
import com.momodding.backend.exception.UnauthorizedException
import com.momodding.backend.utils.AppProperties
import com.momodding.backend.utils.isNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserCredentialServiceImpl @Autowired constructor(
		val userCredentialRepository: UserCredentialRepository,
		val userTokenRepository: UserTokenRepository,
		val jwtUtils: JwtUtils,
		val appProperties: AppProperties
) : UserCredentialService {
	override fun findUserById(id: Long): UserCredential? {
		return userCredentialRepository.findById(id).orElse(null)
	}

	override fun findUserByEmail(email: String): UserCredential? {
		return userCredentialRepository.findByEmail(email)?.orElse(null)
	}

	override fun saveOrUpdate(data: UserCredential): UserCredential {
		return userCredentialRepository.saveAndFlush(data)
	}

	override fun updateLoginAttempt(data: UserCredential, attempt: Long): UserCredential {
		val update = data.apply {
			loginAttempt = attempt
		}
		return userCredentialRepository.saveAndFlush(update)
	}

	override fun updateStatus(data: UserCredential, userStatus: String): UserCredential {
		val update = data.apply {
			status = userStatus
		}
		return userCredentialRepository.saveAndFlush(update)
	}

	override fun doLogin(req: LoginRequest): Any {
		val creds = userCredentialRepository.findByEmail(req.username)

		when(creds) {
			null -> throw DataNotFoundException("user")
			else -> {
				validatePreLogin(creds = creds.get(), req = req)

				val tokenPayload = TokenPayload(
						ucId = creds.get().id!!,
						email = creds.get().email ?: "",
						userRole = creds.get().role,
						issuedAt = Date().time
				)

				val userToken = userTokenRepository.saveAndFlush(UserToken(
						ucId = creds.get().id,
						refreshToken = BCryptPasswordEncoder().encode(tokenPayload.toString()),
						expiredIn = Date(System.currentTimeMillis() + appProperties.jwt.refresh)
				))

				when (userToken) {
					null -> throw AppException("login failed")
					else -> return AuthResponse(
							accessToken = jwtUtils.generateToken(tokenPayload),
							username = creds.get().username,
							email = creds.get().email,
							role = creds.get().role,
							refreshToken = userToken.refreshToken
					)
				}
			}
		}
	}

	override fun doRegister(req: RegisterRequest): Any {
		validatePreRegister(req)
		val data = UserCredential(
				username = req.username,
				email = req.email,
				password = req.password,
				status = "Y",
				role = req.role,
				loginAttempt = 0L
		)
		val save = userCredentialRepository.saveAndFlush(data)
		val tokenPayload = save.id?.let {
			TokenPayload(
					ucId = it,
					email = save.email ?: "",
					userRole = save.role,
					issuedAt = Date().time
			)
		}

		val userToken = userTokenRepository.saveAndFlush(UserToken(
				ucId = save.id,
				refreshToken = BCryptPasswordEncoder().encode(tokenPayload.toString()),
				expiredIn = Date(System.currentTimeMillis() + appProperties.jwt.refresh)
		))

		when (userToken) {
			null -> throw AppException("login failed")
			else -> return AuthResponse(
					accessToken = tokenPayload?.let { jwtUtils.generateToken(it) },
					username = save.username,
					email = save.email,
					role = save.role,
					refreshToken = userToken.refreshToken
			)
		}
	}

	private fun validatePreLogin(creds: UserCredential, req: LoginRequest) {
		if (!"Y".equals(creds.status, true)) {
			creds.loginAttempt?.let { updateLoginAttempt(data = creds, attempt = it+1) }
			throw UnauthorizedException("status not active")
		}
		if (creds.loginAttempt!! > 3L) {
			updateStatus(data = creds, userStatus = "N")
			throw UnauthorizedException("max login attempt reached")
		}
		if (!req.password.equals(creds.password)) {
			creds.loginAttempt?.let { updateLoginAttempt(data = creds, attempt = it+1) }
			throw UnauthorizedException("password mismatch")
		}
		updateLoginAttempt(creds, 0)
	}

	private fun validatePreRegister(req: RegisterRequest) {
		if (findUserByEmail(req.email).isNotNull()) throw AppException("email already used")
	}

}