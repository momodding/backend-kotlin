package com.momodding.backend

import com.momodding.backend.utils.AppProperties
import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.StandardEnvironment

@SpringBootApplication
@EnableConfigurationProperties(AppProperties::class)
class BackendApplication

fun main(args: Array<String>) {
	val dotenv = dotenv().entries().associateByTo(hashMapOf(), {it.key}, {it.value}).toMap()

	SpringApplicationBuilder(BackendApplication::class.java)
			.environment(object : StandardEnvironment() {
				override fun customizePropertySources(propertySources: MutablePropertySources) {
					super.customizePropertySources(propertySources)
					propertySources.addLast(MapPropertySource("dotenvProperties", dotenv))
				}
			})
			.run(*args)
//	runApplication<BackendApplication>(*args)
}
