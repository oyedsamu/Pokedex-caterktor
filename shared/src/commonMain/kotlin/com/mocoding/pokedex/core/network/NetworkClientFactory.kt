package com.mocoding.pokedex.core.network

import io.github.oyedsamu.caterktor.CaterKtor
import io.github.oyedsamu.caterktor.ExperimentalCaterktor
import io.github.oyedsamu.caterktor.NetworkClient
import io.github.oyedsamu.caterktor.logging.LogLevel
import io.github.oyedsamu.caterktor.logging.LoggerInterceptor
import io.github.oyedsamu.caterktor.serialization.json.KotlinxJsonConverter
import kotlinx.serialization.json.Json

@OptIn(ExperimentalCaterktor::class)
internal fun createNetworkClient(enableLogging: Boolean): NetworkClient {
    return CaterKtor {
        transport = createPlatformTransport()
        baseUrl = NetworkConstants.baseUrl
        addConverter(
            KotlinxJsonConverter(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        )

        if (enableLogging) {
            addInterceptor(
                LoggerInterceptor(level = LogLevel.Body) { line ->
                    println(line)
                }
            )
        }
    }
}
