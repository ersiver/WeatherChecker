package com.breiter.weathercheckerapp.model

import com.squareup.moshi.Json

class Main(
    val temp: Double,
    val pressure: Double,
    val humidity: Double,
    @Json(name = "temp_min") val minTemp: Double,
    @Json(name = "temp_max") val maxTemp: Double
)