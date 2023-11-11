package com.lithtml.dev.network.models

import kotlinx.serialization.Serializable


@Serializable
data class MyDataModel(
    val project: String,
    val latest: String,
    val softVersion: String,
    val releaseVersion: Int,
    val publisher: String,
    val releaseDate: String,
    val changeLog: String,
    val history: List<String>
)