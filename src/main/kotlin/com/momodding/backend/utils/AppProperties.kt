package com.momodding.backend.utils

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "app")
data class AppProperties(
		val jwt: JwtProperties,
		val security: SecurityProperties,
		val cloudinary: CloudinaryProperties
)

data class JwtProperties(
		var expiration: Long,
		var refresh: Long,
		var signingKey: String
)

data class SecurityProperties(
		var secret: String,
		var expiration: String,
		var maxLoginAttemp: Int,
		var hmacSecret: String
)

data class CloudinaryProperties(
		var cloud_name: String,
		var api_key: String,
		var api_secret: String
)
