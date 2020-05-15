package com.momodding.backend.config.auth

import io.jsonwebtoken.Claims

data class TokenPayload(
    var ucId: Long,
    var email: String,
    var userRole: Long? = null,
    var issuedAt: Long
)

fun TokenPayload.toMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>(
        "ucId" to this.ucId,
        "email" to this.email,
        "issuedAt" to this.issuedAt
    )

    this.userRole?.let { map["userRole"] = it }

    return map
}

fun Claims.toObject(): TokenPayload {
    val tokenPayload = TokenPayload(
        ucId = (this["ucId"] as Int).toLong(),
        email = this["email"] as String,
        issuedAt = this["issuedAt"] as Long
    )
    this["userRole"]?.let { ur -> tokenPayload.apply { userRole = (ur as Int).toLong() } }

    return tokenPayload
}