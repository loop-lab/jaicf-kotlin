package com.justai.jaicf.activator.rasa.api

import com.justai.jaicf.helpers.http.toUrl
import com.justai.jaicf.helpers.logging.WithLogger
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.*
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class RasaApi(
    private val uri: String
) : WithLogger {

    internal val Json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val client = HttpClient(CIO) {
        expectSuccess = true

        install(JsonFeature) {
            serializer = KotlinxSerializer(Json)
        }

        install(Logging) {
            level = LogLevel.INFO
        }
    }

    fun parseMessage(request: RasaParseMessageRequest): JsonObject? = runBlocking {
        try {
            client.post<JsonObject>("$uri/model/parse".toUrl()) {
                contentType(ContentType.Application.Json)
                body = request
            }
        } catch (e: Exception) {
            logger.error("Cannot parse $request", e)
            null
        }

    }
}