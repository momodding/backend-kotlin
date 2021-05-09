package com.momodding.backend.config.auth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.momodding.backend.app.service.usercredential.UserCredentialService
import com.momodding.backend.exception.UnauthorizedException
import id.investree.app.config.base.MetaResponse
import id.investree.app.config.base.ResultResponse
import io.jsonwebtoken.ExpiredJwtException
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
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

    @Throws(UnauthorizedException::class)
    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        val accessToken = req.getHeader("X-Token-Auth")
        var email: String? = null
        val result = ResultResponse<Any>(
            status = "ERROR",
            meta = MetaResponse(
                code = HttpStatus.SC_UNAUTHORIZED,
                message = "Unauthorized action"
            )
        )

        if (!accessToken.isNullOrBlank()) {
            try {
                email = jwtUtils.getEmailFromToken(accessToken)
            } catch (e: Exception) {
                when (e) {
                    is IllegalArgumentException ->
                        logger.error("an error occured during getting credential from token")
                    is ExpiredJwtException ->
                        logger.error("the token is expired and not valid anymore")
                    is SignatureException ->
                        logger.error("Authentication Failed. Username or Password not valid.")
                }

                with(res) {
                    status = HttpStatus.SC_UNAUTHORIZED
                    contentType = MediaType.APPLICATION_JSON_VALUE
                    characterEncoding = "UTF-8"
                    writer.write(jacksonObjectMapper().writeValueAsString(result))
                }
                return
            }

            val creds = email?.let { userCredentialService.findUserByEmail(it) }
            if (Optional.ofNullable(creds).isPresent) {
                val authentication = UsernamePasswordAuthenticationToken(
                    userCredentialService,
                    null, mutableListOf(SimpleGrantedAuthority(creds?.role.toString()))
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(req)
                SecurityContextHolder.getContext().authentication = authentication
                logger.error("Authentication success")
            } else {
                logger.error("Authentication failed")
                with(res) {
                    status = HttpStatus.SC_UNAUTHORIZED
                    contentType = MediaType.APPLICATION_JSON_VALUE
                    characterEncoding = "UTF-8"
                    writer.write(jacksonObjectMapper().writeValueAsString(result))
                }
                return
            }
        }

        chain.doFilter(req, res)
    }

}