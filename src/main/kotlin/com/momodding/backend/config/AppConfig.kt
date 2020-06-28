package com.momodding.backend.config

import com.cloudinary.Cloudinary
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.momodding.backend.utils.AppProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*


@Configuration
class AppConfig constructor(
        val appProperties: AppProperties
) {

    @Bean
    fun objectMapper() = ObjectMapper().apply {
        registerModule(KotlinModule())
    }

    @Bean
    fun cloudinaryConfig(): Cloudinary {
        var cloudinary: Cloudinary? = null
        val config: MutableMap<String, String> = HashMap()
        config["cloud_name"] = appProperties.cloudinary.cloud_name
        config["api_key"] = appProperties.cloudinary.api_key
        config["api_secret"] = appProperties.cloudinary.api_secret
        cloudinary = Cloudinary(config)
        return cloudinary
    }
}