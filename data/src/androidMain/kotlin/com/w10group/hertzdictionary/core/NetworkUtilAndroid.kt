package com.w10group.hertzdictionary.core

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

/**
 * Android 端 Ktor HttpClient
 * @author qiaoyuang
 */

actual val CLIENT = okHttp()

private fun okHttp(): HttpClient = HttpClient(OkHttp) {
    configJson()
}