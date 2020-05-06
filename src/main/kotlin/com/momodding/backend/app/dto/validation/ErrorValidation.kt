package com.momodding.backend.app.dto.validation

import com.fasterxml.jackson.annotation.JsonProperty

data class ErrorValidation(
		@JsonProperty(value = "field")
		val field: String?,

		@JsonProperty(value = "message")
		val message: String?
)
