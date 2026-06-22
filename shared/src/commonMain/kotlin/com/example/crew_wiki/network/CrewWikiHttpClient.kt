package com.example.crew_wiki.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

const val BASE_URL = "http://3.35.253.192:8080"

fun createCrewWikiHttpClient(): HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                isLenient = true
                coerceInputValues = true
            },
        )
    }
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                println("[CrewWiki HTTP] $message")
            }
        }
        level = LogLevel.INFO
    }
}
