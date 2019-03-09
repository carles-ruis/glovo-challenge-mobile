package com.glovo.challenge.model

data class City(
    val code: String,
    val name: String? = null,
    val countryCode: String? = null,
    val currency: String? = null,
    val enabled: Boolean? = null,
    val busy: Boolean? = null,
    val timeZone: String? = null,
    val workingArea: List<String> = emptyList()
)