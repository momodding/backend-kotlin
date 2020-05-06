package com.momodding.backend.app.controller.v1.auth

import com.momodding.backend.app.dto.request.LoginRequest
import com.momodding.backend.app.dto.request.RegisterRequest
import com.momodding.backend.app.service.usercredential.UserCredentialService
import com.momodding.backend.exception.FormValidationException
import com.momodding.backend.utils.generateResponse
import id.investree.app.config.base.BaseController
import id.investree.app.config.base.ResultResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping(value = ["v1/auth"], produces = [MediaType.APPLICATION_JSON_VALUE])
class AuthController @Autowired constructor(
		val userCredentialService: UserCredentialService,
		val authValidation: AuthValidation
) : BaseController() {

	@PostMapping("login")
	fun login (@Valid @RequestBody req: LoginRequest, error: Errors) : ResponseEntity<ResultResponse<Any>> {
		authValidation.validateLogin(req, error)
		if (error.hasErrors()) throw FormValidationException(error.generateResponse())
		return generateResponse(userCredentialService.doLogin(req)).done("Login sukses")
	}

	@PostMapping("register")
	fun register (@Valid @RequestBody req: RegisterRequest, error: Errors) : ResponseEntity<ResultResponse<Any>> {
		authValidation.validateRegister(req, error)
		if (error.hasErrors()) {
			throw FormValidationException(error.generateResponse())
		}
		return generateResponse(userCredentialService.doRegister(req)).done("register sukses")
	}
}