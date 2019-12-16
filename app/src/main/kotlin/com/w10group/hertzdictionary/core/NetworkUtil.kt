package com.w10group.hertzdictionary.core

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

/**
 * Created by Administrator on 2018/2/6 0006.
 * Ktor HttpClient
 */

val CLIENT = okHttp()

private fun okHttp(): HttpClient = HttpClient(OkHttp) {
    configJson()
}

/*@UseExperimental(KtorExperimentalAPI::class)
private fun cio(): HttpClient = HttpClient(CIO) {
    engine {
        maxConnectionsCount = 1000
        endpoint {
            maxConnectionsPerRoute = 100
            pipelineMaxSize = 20
            keepAliveTime = 5000
            connectTimeout = 5000
            connectRetryAttempts = 5
        }
    }
    configJson()
}*/

@UseExperimental(UnstableDefault::class)
private fun <T : HttpClientEngineConfig> HttpClientConfig<T>.configJson() =
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json.nonstrict)
        }