package com.momodding.backend.config.websecurity

import com.momodding.backend.exception.AppException
import org.springframework.http.HttpMethod
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CorsFilter : Filter {

	@Throws(AppException::class)
	override fun doFilter(req: ServletRequest, res: ServletResponse, chainFilter: FilterChain) {
		println("Filtering on...........................................................")
		val response = res as HttpServletResponse
		response.setHeader("Access-Control-Allow-Origin", "*")
		response.setHeader("Access-Control-Allow-Credentials", "true")
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE")
		response.setHeader("Access-Control-Max-Age", "3600")
		response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Authorization, Origin, Accept, Access-Control-Request-Method, Access-Control-Request-Headers, X-Token-Auth")

		if (HttpMethod.OPTIONS.name == (req as HttpServletRequest).method) {
			response.status = HttpServletResponse.SC_OK;
		} else {
			chainFilter.doFilter(req, res)
		}
	}
}