package com.momodding.backend

import com.momodding.backend.utils.AppProperties
import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.StandardEnvironment

@SpringBootApplication
@EnableConfigurationProperties(AppProperties::class)
class BackendApplication

fun main(args: Array<String>) {
	runApplication<BackendApplication>(*args)
}
