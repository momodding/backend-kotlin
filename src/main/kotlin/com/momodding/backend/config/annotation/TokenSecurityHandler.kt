package com.momodding.backend.config.annotation

import com.momodding.backend.app.service.usercredential.UserCredentialService
import com.momodding.backend.config.auth.JwtUtils
import com.momodding.backend.exception.UnauthorizedException
import io.jsonwebtoken.ExpiredJwtException
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.security.SignatureException
import javax.servlet.http.HttpServletRequest

@Aspect
@Component
class TokenSecurityHandler @Autowired constructor(
    private val request: HttpServletRequest,
    private val jwtUtils: JwtUtils,
    val userCredentialService: UserCredentialService
) {

    @Before("@within(TokenSecurity)")
    fun withinHandler(joinPoint: JoinPoint) {
        handler(joinPoint)
    }

    @Before("@annotation(TokenSecurity)")
    fun annotationHandler(joinPoint: JoinPoint) {
        handler(joinPoint)
    }

    private fun handler(joinPoint: JoinPoint) {
        val accessToken = request.getHeader("X-Token-Auth")
        val email = when {
            accessToken.isNullOrBlank() -> throw UnauthorizedException("Token not found")
            else -> {
                try {
                    jwtUtils.getEmailFromToken(accessToken)
                } catch (e: IllegalArgumentException) {
                    throw UnauthorizedException("Invalid token")
                } catch (e: ExpiredJwtException) {
                    throw UnauthorizedException("Session expired")
                } catch (e: SignatureException) {
                    throw UnauthorizedException("Invalid credential")
                }
            }
        }

        email?.let { userCredentialService.findUserByEmail(it) } ?: throw UnauthorizedException("Invalid token")
    }
}