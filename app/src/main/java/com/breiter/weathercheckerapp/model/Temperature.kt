package com.breiter.weathercheckerapp.model

import com.squareup.moshi.Json

class Temperature(
    @Json(name = "day") val tempDay: Double,
    @Json(name = "night") val tempNight: Double
)
