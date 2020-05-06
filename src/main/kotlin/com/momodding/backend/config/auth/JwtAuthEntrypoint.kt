package com.momodding.backend.config.auth

import id.investree.app.config.base.MetaResponse
import id.investree.app.config.base.ResultResponse
import org.springframework.http.HttpStatus
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
			status = org.springframework.http.HttpStatus.UNAUTHORIZED.value()
			contentType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE
			characterEncoding = "UTF-8"
			writer.write(com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(result))
		}
	}

}