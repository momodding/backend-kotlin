package com.momodding.backend.app.controller.v1.auth

import com.momodding.backend.app.dto.request.LoginRequest
import com.momodding.backend.app.dto.request.RegisterRequest
import com.momodding.backend.app.service.usercredential.UserCredentialService
import com.momodding.backend.utils.isValidEmail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.validation.Errors

@Component
class AuthValidation @Autowired constructor(
		val userCredentialService: UserCredentialService
) {
	fun validateLogin(request: LoginRequest, errors: Errors) {
		if (!request.username.isValidEmail()) errors.reject("username", "invalid email")
	}

	fun validateRegister(request: RegisterRequest, errors: Errors) {
		if (!request.email.isValidEmail()) errors.reject("email", "invalid email")

		if (!request.role.equals(1L) && !request.role.equals(2L)) errors.reject("role", "invalid role")
	}
}