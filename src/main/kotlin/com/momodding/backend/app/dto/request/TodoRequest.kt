package com.momodding.backend.app.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class TodoRequest (
	@JsonProperty(value = "name")
	@field:NotNull
	@field:NotBlank
	@field:NotEmpty
	val name: String?,

	@JsonProperty(value = "description")
	@field:NotNull
	@field:NotBlank
	@field:NotEmpty
	val description: String?
)