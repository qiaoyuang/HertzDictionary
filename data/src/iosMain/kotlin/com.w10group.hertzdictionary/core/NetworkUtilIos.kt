package com.w10group.hertzdictionary.core

import io.ktor.client.HttpClient
import io.ktor.client.engine.ios.Ios

actual val CLIENT = nsUrlSession()

private fun nsUrlSession(): HttpClient = HttpClient(Ios) {
    configJson()
    engine {
        configureRequest {
            setAllowsCellularAccess(true)
        }
    }
}