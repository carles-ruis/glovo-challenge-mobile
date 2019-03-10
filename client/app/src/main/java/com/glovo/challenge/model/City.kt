package com.glovo.challenge.model

data class City(
    val code: String,
    val name: String,
    val countryCode: String,
    var currency: String? = null,
    val enabled: Boolean? = null,
    val busy: Boolean? = null,
    var timeZone: String? = null,
    val workingArea: WorkingArea)