package com.breiter.weathercheckerapp.model

import com.squareup.moshi.Json

class ForecastItem(
    val temp: Temperature,
    @Json(name = "dt") val dateTime: Long,
    @Json(name = "weather") val list: List<WeatherItem>
)