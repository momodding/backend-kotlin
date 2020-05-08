package com.momodding.backend.config.graphql

import com.momodding.backend.config.auth.TokenPayload
import graphql.servlet.GraphQLContext
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.websocket.server.HandshakeRequest

class AuthGraphQLContext : GraphQLContext {

	private lateinit var tokenPayload: TokenPayload

	constructor()

	constructor(handshake: HandshakeRequest?)

	constructor(token: TokenPayload,
	            request: HttpServletRequest?) : super(request) {
		this.tokenPayload = token
	}

	fun getTokenPayload() : TokenPayload = tokenPayload
}