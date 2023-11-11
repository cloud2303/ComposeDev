package com.lithtml.dev.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
object NetworkModule {
    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json()
        }
       defaultRequest {
           url("https://bt.lithtml.com:5555")
//           /mcu/update_mcu_v21.json
       }
    }
}