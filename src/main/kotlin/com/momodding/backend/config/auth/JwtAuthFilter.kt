package com.momodding.backend.config.auth

import com.momodding.backend.app.service.usercredential.UserCredentialService
import com.momodding.backend.exception.AppException
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import java.security.SignatureException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class JwtAuthFilter @Autowired constructor(
		val userCredentialService: UserCredentialService,
		val jwtUtils: JwtUtils
) : OncePerRequestFilter() {

	@Throws(AppException::class)
	override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
		val accessToken = req.getHeader("X-Token-Auth")
		var email: String? = null

		if (!accessToken.isNullOrBlank()) {
			try {
				email = jwtUtils.getEmailFromToken(accessToken)
			} catch (e: IllegalArgumentException) {
				logger.error("an error occured during getting credential from token")

			} catch (e: ExpiredJwtException) {
				logger.error("the token is expired and not valid anymore")
			} catch (e: SignatureException) {
				logger.error("Authentication Failed. Username or Password not valid.")
			}
		} else {
			logger.error("Authentication Failed. Username or Password not valid.")
		}

		if (!email.isNullOrBlank()) {
			val creds = email.let { userCredentialService.findUserByEmail(it) }
			if (Optional.ofNullable(creds).isPresent) {
				val authentication = UsernamePasswordAuthenticationToken(userCredentialService,
						null, mutableListOf(SimpleGrantedAuthority(creds?.role.toString())))
				authentication.details = WebAuthenticationDetailsSource().buildDetails(req)
				logger.error("Authentication Failed. Username or Password not valid.")
				SecurityContextHolder.getContext().authentication = authentication
			}
		}

		chain.doFilter(req, res)
	}

}