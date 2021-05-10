package com.momodding.backend.config.graphql

import com.momodding.backend.config.auth.JwtUtils
import com.momodding.backend.config.auth.toObject
import com.momodding.backend.graphql.exception.UnauthorizeException
import graphql.servlet.GraphQLContext
import graphql.servlet.GraphQLContextBuilder
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.websocket.server.HandshakeRequest

@Component
class AuthGraphQLContextBuilder(
		val jwtUtils: JwtUtils
) : GraphQLContextBuilder {

	override fun build(request: HttpServletRequest?): AuthGraphQLContext? {
		val req = request?.getHeader("X-Token-Auth")
		when (req) {
			null -> throw UnauthorizeException("graphqls")
		}
		return req?.let { it -> jwtUtils.getAllClaimFromToken(it)?.toObject()?.let { AuthGraphQLContext(it, request) } }
	}

	override fun build(handshake: HandshakeRequest?): GraphQLContext {
		return AuthGraphQLContext(handshake)
	}

	override fun build(): GraphQLContext {
		return AuthGraphQLContext()
	}

}