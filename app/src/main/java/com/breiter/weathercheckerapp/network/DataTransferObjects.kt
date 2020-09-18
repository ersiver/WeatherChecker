package com.breiter.weathercheckerapp.network

import com.breiter.weathercheckerapp.domain.WeatherItem
import com.breiter.weathercheckerapp.domain.CurrentWeather
import com.breiter.weathercheckerapp.domain.ForecastItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
* DataTransferObjects (DTO) are responsible for parsing responses from the network.
*/
@JsonClass(generateAdapter = true)
data class CurrentWeatherDTO(
    @Json(name = "name") val city: String,
    @Json(name = "dt") val dateTime: Long,
    val main: MainDTO,
    val wind: WindDTO,
    @Json(name = "weather") val weathers: List<WeatherItemDTO>
)

@JsonClass(generateAdapter = true)
data class MainDTO(
    val temp: Double,
    val pressure: Double,
    val humidity: Double,
    @Json(name = "temp_min") val minTemp: Double,
    @Json(name = "temp_max") val maxTemp: Double
)

@JsonClass(generateAdapter = true)
data class WindDTO(val speed: Double)

@JsonClass(generateAdapter = true)
data class TemperatureDTO(
    @Json(name = "day") val tempDay: Double,
    @Json(name = "night") val tempNight: Double
)

@JsonClass(generateAdapter = true)
data class ForecastDTO(@Json(name = "list") val forecasts: List<ForecastItemDTO>)

@JsonClass(generateAdapter = true)
data class ForecastItemDTO(
    val temp: TemperatureDTO,
    @Json(name = "dt") val dateTime: Long,
    @Json(name = "weather") val weathers: List<WeatherItemDTO>
)

@JsonClass(generateAdapter = true)
data class WeatherItemDTO(
    val description: String,
    @Json(name = "icon") val iconId: String
)

/**
 * Convert DTO to domain CurrentWeather object
 */
fun CurrentWeatherDTO.asDomainModel(): CurrentWeather {
    return CurrentWeather(
        city = city,
        dateTime = dateTime,
        temp = main.temp,
        pressure = main.pressure,
        humidity = main.humidity,
        minTemp = main.minTemp,
        maxTemp = main.maxTemp,
        windSpeed = wind.speed,
        weathers = weathers.map {
            WeatherItem(
                description = it.description,
                iconId = it.iconId
            )
        }
    )
}

/**
 * Convert DTO to a list of domain Forecast objects.
 */
fun ForecastDTO.asDomainModel(): List<ForecastItem> {
    return forecasts.map {
        ForecastItem(
            tempDay = it.temp.tempDay,
            tempNight = it.temp.tempNight,
            dateTime = it.dateTime,
            weathers = it.weathers.map { weatherItem ->
                WeatherItem(
                    description = weatherItem.description,
                    iconId = weatherItem.iconId
                )
            }
        )
    }
}