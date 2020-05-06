package com.momodding.backend.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

	@Bean
	fun objectMapper() = ObjectMapper().apply {
		registerModule(KotlinModule())
	}
}