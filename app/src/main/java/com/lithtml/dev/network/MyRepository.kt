package com.lithtml.dev.network

import com.lithtml.dev.network.models.MyDataModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class MyRepository(private val client: HttpClient = NetworkModule.client) {
    suspend fun fetchSomeData(): MyDataModel {
        return client.get("/mcu/update_mcu_v21.json").body()
    }
}