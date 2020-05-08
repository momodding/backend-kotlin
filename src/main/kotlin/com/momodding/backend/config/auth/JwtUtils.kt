package com.momodding.backend.config.auth

import com.momodding.backend.utils.AppProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.io.Serializable
import java.security.Key
import java.util.*
import java.util.function.Function
import javax.crypto.spec.SecretKeySpec
import javax.servlet.http.HttpServletRequest

@Component
class JwtUtils(
		val appProperties: AppProperties
) : Serializable {

	fun getLoginId(http: HttpServletRequest): Long? =
			getClaimFromToken(http, Function { it.toObject().ucId })

	fun getEmailFromToken(token: String): String? =
		getClaimFromToken(token, Function { it.subject })

	fun getRoleFromToken(token: String): Long? =
		getClaimFromToken(token, Function { it.toObject().userRole })

	fun getExpiredAtFromToken(token: String): Date? =
		getClaimFromToken(token, Function { it.expiration })

	fun String?.isTokenExpired() : Boolean = when(this) {
		null -> false
		else -> Date().after(getExpiredAtFromToken(this))
	}

	fun <T> getClaimFromToken(token: String, claimsResolver: Function<Claims, T>): T? {
		val claims = getAllClaimFromToken(token)
		return claims?.let { claimsResolver.apply(it) }
	}

	fun <T> getClaimFromToken(http: HttpServletRequest, claimsResolver: Function<Claims, T>): T? {
		val claims = getAllClaimFromToken(http.getHeader("X-Token-Auth"))
		return claims?.let { claimsResolver.apply(it) }
	}

	fun getAllClaimFromToken(token: String) : Claims? =
			Jwts.parserBuilder().setSigningKey(generateKey()).build().parseClaimsJws(token)
					.body

	fun getAllClaimFromToken(http: HttpServletRequest) : Claims? =
			Jwts.parserBuilder().setSigningKey(generateKey()).build().parseClaimsJws(http.getHeader("X-Token-Auth"))
					.body

	fun generateToken(payload: TokenPayload) : String {
		return doGenerateToken(payload.toMap(), payload.email)
	}

	fun doGenerateToken(claims: Map<String, Any>, subject: String) : String =
			Jwts.builder().setClaims(claims.toMap()).setSubject(subject).setIssuedAt(Date(System.currentTimeMillis()))
					.setExpiration(
							Date(System.currentTimeMillis() + appProperties.jwt.expiration)
					).signWith(generateKey()).compact()

	private fun generateKey(): Key =
			SecretKeySpec(appProperties.jwt.signingKey.toByteArray(), SignatureAlgorithm.HS512.jcaName)
}