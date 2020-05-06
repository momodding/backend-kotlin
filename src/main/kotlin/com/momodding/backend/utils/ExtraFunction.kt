package com.momodding.backend.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.momodding.backend.app.dto.validation.ErrorValidation
import org.springframework.validation.Errors
import java.util.regex.Pattern
import java.util.stream.Collectors

fun Any?.isNotNull() : Boolean = when(this) {
	null -> false
	else -> true
}

fun String?.isContainNumber() : Boolean = when(this) {
	null -> false
	else -> Pattern.compile( "[0-9]" ).matcher( this ).find()
}

fun String?.isValidEmail() : Boolean = when(this) {
	null -> false
	else -> !this.isNullOrBlank() && Pattern.compile( "^(.+)@(.+)\$" ).matcher( this ).find()
}

fun Errors.generateResponse() : String {
	return jacksonObjectMapper().writeValueAsString(this.allErrors
			.stream()
			.map { x ->
				ErrorValidation(
						field = x.code,
						message = x.defaultMessage
				)
			}
			.collect(Collectors.toList()))
}