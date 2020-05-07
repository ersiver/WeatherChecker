package com.breiter.weathercheckerapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.breiter.weathercheckerapp.domain.CurrentWeather
import com.breiter.weathercheckerapp.domain.ForecastItem
import com.breiter.weathercheckerapp.network.CurrentWeatherDTO
import com.breiter.weathercheckerapp.network.WeatherApi
import com.breiter.weathercheckerapp.network.asDomainModel

class WeatherRepository {

    private val _currentWeather = MutableLiveData<CurrentWeatherDTO>()
    val currentWeather: LiveData<CurrentWeather> = Transformations.map(_currentWeather) {
        it.asDomainModel()
    }

    private val _forecasts = MutableLiveData<List<ForecastItem>>()
    val forecasts: LiveData<List<ForecastItem>>
        get() = _forecasts

    /**
     * Gets weather information for current location from the
     * Weather API service and updates the weather and forecast LiveData.
     * Retrofit makes suspending functions main-safe.
     */
    suspend fun getWeatherAndForecasts(lat: Double, lon: Double) {
        try {
            _currentWeather.value = WeatherApi.retrofitService.getCurrentWeather(lat, lon)
            getForecast(lat, lon)
        } catch (t: Throwable) {
            throw Throwable(t)
        }
    }

    private suspend fun getForecast(lat: Double, lon: Double) {
        try {
            val forecastResponse = WeatherApi.retrofitService.getForecast(lat, lon)
            _forecasts.value = forecastResponse.asDomainModel()
        } catch (t: Throwable) {
            throw Throwable(t)
        }
    }

    /**
     * Gets weather information for user's input from Weather API Retrofit.
     * service and updates the weather and forecast LiveData.
     */

    suspend fun getWeatherAndForecasts(cityName: String) {
        try {
            _currentWeather.value = WeatherApi.retrofitService.getCurrentWeather(cityName)
            getForecast(cityName)
        } catch (t: Throwable) {
            throw Throwable(t)
        }
    }

    private suspend fun getForecast(cityName: String) {
        try {
            val forecastResponse = WeatherApi.retrofitService.getForecast(cityName)
            _forecasts.value = forecastResponse.asDomainModel()
        } catch (t: Throwable) {
            throw Throwable(t)
        }
    }
}

