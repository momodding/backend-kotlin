package com.momodding.backend.app.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class RegisterRequest (
		@field:NotNull(message = "username can't be null")
		@field:NotBlank(message = "username can't be blank")
		@JsonProperty("username")
		val username: String,

		@field:NotNull(message = "email can't be null")
		@field:NotBlank(message = "email can't be blank")
		@JsonProperty("email")
		val email: String,

		@field:NotNull(message = "password can't be null")
		@field:NotBlank(message = "password can't be blank")
		@JsonProperty("password")
		val password: String,

		@field:NotNull(message = "role can't be null")
		@JsonProperty("role")
		val role: Long
)