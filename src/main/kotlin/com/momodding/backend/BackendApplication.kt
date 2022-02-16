package com.momodding.backend

import com.momodding.backend.utils.AppProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableConfigurationProperties(AppProperties::class)
@EnableTransactionManagement
class BackendApplication

fun main(args: Array<String>) {
	runApplication<BackendApplication>(*args)
}
