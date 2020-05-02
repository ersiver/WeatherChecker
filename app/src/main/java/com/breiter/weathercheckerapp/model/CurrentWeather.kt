package com.breiter.weathercheckerapp.model

import com.squareup.moshi.Json

class CurrentWeather(
    @Json(name = "name") val city: String,
    @Json(name = "dt") val dateTime: Long,
    val main: Main,
    val wind: Wind,
    @Json(name = "weather") val list: List<WeatherItem>
)