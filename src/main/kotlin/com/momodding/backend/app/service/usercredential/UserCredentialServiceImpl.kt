package com.momodding.backend.app.service.usercredential

import com.momodding.backend.app.dto.request.LoginRequest
import com.momodding.backend.app.dto.request.RegisterRequest
import com.momodding.backend.app.entity.UserCredential
import com.momodding.backend.app.repository.UserCredentialRepository
import com.momodding.backend.config.auth.JwtUtils
import com.momodding.backend.config.auth.TokenPayload
import com.momodding.backend.exception.AppException
import com.momodding.backend.exception.DataNotFoundException
import com.momodding.backend.exception.UnauthorizedException
import com.momodding.backend.utils.isNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap

@Service
class UserCredentialServiceImpl @Autowired constructor(
		val userCredentialRepository: UserCredentialRepository,
		val jwtUtils: JwtUtils
) : UserCredentialService {
	override fun findUserByEmail(email: String): UserCredential {
		return userCredentialRepository.findByEmail(email).orElse(null)
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

				val tokenPayload = creds.get().id?.let {
					TokenPayload(
							loginId = it,
							email = creds.get().email?: "",
							userRole = creds.get().role,
							issuedAt = Date().time
					)
				}

				//TODO [UTSMAN] create proper response
				val response = HashMap<String, Any>()
				tokenPayload?.let { jwtUtils.generateToken(it) }?.let { response.put("accessToken", it) }
				response.put("credentials", creds.get())

				return response
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
					loginId = it,
					email = save.email?: "",
					userRole = save.role,
					issuedAt = Date().time
			)
		}

		val response = HashMap<String, Any>()
		tokenPayload?.let { jwtUtils.generateToken(it) }?.let { response.put("accessToken", it) }
		response.put("credentials", save)

		return response
	}

	private fun validatePreLogin(creds: UserCredential, req: LoginRequest) {
		if (!"Y".equals(creds.status, true)) {
			creds.loginAttempt?.let { updateLoginAttempt(data = creds, attempt = it+1) }
			throw UnauthorizedException("status not active")
		}
		if (creds.loginAttempt!!.compareTo(3L) > 1) {
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