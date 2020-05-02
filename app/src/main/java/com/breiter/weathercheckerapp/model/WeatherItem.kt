package com.breiter.weathercheckerapp.model

import com.squareup.moshi.Json

class WeatherItem(
    val description: String,
    @Json(name = "icon") val iconId: String
)