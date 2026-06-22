package com.example.crew_wiki.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

const val BASE_URL = "https://api.crew-wiki.site"

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
    // 비2xx 응답 시 JSON 파싱 시도 전에 예외 발생
    HttpResponseValidator {
        validateResponse { response ->
            if (!response.status.isSuccess()) {
                throw CrewWikiApiException(
                    statusCode = response.status.value,
                    message = "서버 오류: HTTP ${response.status.value}",
                )
            }
        }
    }
}

class CrewWikiApiException(
    val statusCode: Int,
    message: String,
) : Exception(message)
