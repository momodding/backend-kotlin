package com.momodding.backend.exception

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.momodding.backend.app.dto.validation.ErrorValidation
import id.investree.app.config.base.AbstractResponseHandler
import id.investree.app.config.base.MetaResponse
import id.investree.app.config.base.ResultResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.stream.Collectors

@ControllerAdvice
class CustomExceptionHandler : ResponseEntityExceptionHandler() {

	override fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException,
	                                          headers: HttpHeaders,
	                                          status: HttpStatus,
	                                          request: WebRequest): ResponseEntity<Any> {
		val errors = ex.bindingResult.fieldErrors
				.stream()
				.map { x ->
					ErrorValidation(
							field = x.field,
							message = x.defaultMessage
					)
				}
				.collect(Collectors.toList())

		val metaResponse = MetaResponse(
				code = HttpStatus.UNPROCESSABLE_ENTITY.value(),
				message = "Mandatory Params is not fullfilled, see doc for any of mandatory params required!",
				debugInfo = jacksonObjectMapper().writeValueAsString(errors)
		)

		val resultResponse = ResultResponse(
				status = "ERROR",
				data = null,
				meta = metaResponse
		)
		return ResponseEntity(resultResponse, HttpStatus.UNPROCESSABLE_ENTITY)
	}

	override fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException,
	                                          headers: HttpHeaders,
	                                          status: HttpStatus,
	                                          request: WebRequest): ResponseEntity<Any> {
		val metaResponse = MetaResponse(
				code = HttpStatus.BAD_REQUEST.value(),
				message = ex.localizedMessage
		)

		val resultResponse = ResultResponse(
				status = "ERROR",
				data = null,
				meta = metaResponse
		)
		return ResponseEntity(resultResponse, HttpStatus.BAD_REQUEST)
	}

	override fun handleNoHandlerFoundException(ex: NoHandlerFoundException,
											   headers: HttpHeaders,
											   status: HttpStatus,
											   request: WebRequest): ResponseEntity<Any> {
		val metaResponse = MetaResponse(
				code = HttpStatus.NOT_FOUND.value(),
				message = ex.localizedMessage
		)

		val resultResponse = ResultResponse(
				status = "ERROR",
				data = null,
				meta = metaResponse
		)
		return ResponseEntity(resultResponse, HttpStatus.NOT_FOUND)
	}

	@ExceptionHandler(NoSuchElementException::class)
	fun noSuchElementException(ex: NoSuchElementException) = throwException(ex, HttpStatus.INTERNAL_SERVER_ERROR)

	@ExceptionHandler(AppException::class)
	fun appException(ex: AppException) = throwException(ex, HttpStatus.BAD_REQUEST)

	@ExceptionHandler(UnauthorizedException::class)
	fun unauthorizedException(ex: UnauthorizedException) = throwException(ex, HttpStatus.UNAUTHORIZED)

	@ExceptionHandler(FormValidationException::class)
	fun formValidationException(ex: FormValidationException) = throwException(ex, HttpStatus.UNPROCESSABLE_ENTITY)

	@ExceptionHandler(DataNotFoundException::class)
	fun formValidationException(ex: DataNotFoundException) = throwException(ex, HttpStatus.NOT_FOUND)

	private fun throwException(throwable: Any, httpStatus: HttpStatus, messageId: String? = null) =
			object : AbstractResponseHandler() {
				override fun data(): Any = throwable
			}.done(
					msg = when (messageId) {
						null -> when (throwable) {
							is Throwable -> throwable.localizedMessage
							else -> "Unknown Error"
						}
						else -> "Error occured"
					}, httpStatus = httpStatus
			)
}