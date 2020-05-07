package com.breiter.weathercheckerapp.domain


data class CurrentWeather(
    val city: String,
    val dateTime: Long,
    val temp: Double,
    val pressure: Double,
    val humidity: Double,
    val minTemp: Double,
    val maxTemp: Double,
    val windSpeed: Double,
    val weathers: List<WeatherItem>
)

data class ForecastItem(
    val tempDay: Double,
    val tempNight: Double,
    val dateTime: Long,
    val weathers: List<WeatherItem>
)

data class WeatherItem(
    val description: String,
    val iconId: String
)

