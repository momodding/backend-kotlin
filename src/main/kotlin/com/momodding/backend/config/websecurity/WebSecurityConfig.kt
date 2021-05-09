package com.momodding.backend.config.websecurity

import com.momodding.backend.config.auth.JwtAuthEntrypoint
import com.momodding.backend.config.auth.JwtAuthFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class WebSecurityConfig @Autowired constructor(
		val jwtAuthEntrypoint: JwtAuthEntrypoint,
		val jwtAuthFilter: JwtAuthFilter
) : WebSecurityConfigurerAdapter() {

	private val whitelistEndpoint = arrayOf(
			"/v1/auth/**",
			"/actuator/**",
			"/api-ui/**",
			"/api-docs/**",
			"/graphiql**",
			"/graphql**",
			"/vendor/**"
	)

	override fun configure(http: HttpSecurity) {
		with(http) {
			cors()
				.and()
					.csrf()
					.disable()

			exceptionHandling()
					.authenticationEntryPoint(jwtAuthEntrypoint)
					.and()

			sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
			authorizeRequests()
				.antMatchers(*whitelistEndpoint).permitAll()
				.anyRequest().authenticated()
				.and()
				.addFilter(jwtAuthFilter)
		}
	}

	@Bean
	fun corsConfigurationSource(): CorsConfigurationSource {
		var corsConfiguration = CorsConfiguration().apply {
			addAllowedOrigin("*")
			addAllowedHeader("*")
			addAllowedMethod("*")
			maxAge = 3600L
		}
		return UrlBasedCorsConfigurationSource().apply {
			corsConfiguration = corsConfiguration
			registerCorsConfiguration("/**", corsConfiguration)
		}
	}
}