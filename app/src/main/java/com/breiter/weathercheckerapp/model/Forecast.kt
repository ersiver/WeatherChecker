package com.breiter.weathercheckerapp.model

import com.squareup.moshi.Json

class Forecast(@Json(name = "list") val list: List<ForecastItem>)
