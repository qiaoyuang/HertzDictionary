package com.w10group.hertzdictionary.core

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

/**
 * Created by Administrator on 2018/2/6 0006.
 * Ktor HttpClient
 */

@UseExperimental(KtorExperimentalAPI::class, UnstableDefault::class)
val CLIENT = HttpClient(OkHttp) {
    install(JsonFeature) {
        serializer = KotlinxSerializer(Json.nonstrict)
    }
}