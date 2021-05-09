package com.momodding.backend.config.auth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import id.investree.app.config.base.MetaResponse
import id.investree.app.config.base.ResultResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.Serializable
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthEntrypoint : AuthenticationEntryPoint, Serializable {
	override fun commence(req: HttpServletRequest, res: HttpServletResponse, authException: AuthenticationException) {
		val result = ResultResponse<Any>(
				status = "ERROR",
				meta = MetaResponse(
						code = HttpStatus.UNAUTHORIZED.value(),
						message = "Unauthorized action"
				)
		)

		with(res) {
			status = HttpStatus.UNAUTHORIZED.value()
			contentType = MediaType.APPLICATION_JSON_VALUE
			characterEncoding = "UTF-8"
			writer.write(jacksonObjectMapper().writeValueAsString(result))
		}
	}

}