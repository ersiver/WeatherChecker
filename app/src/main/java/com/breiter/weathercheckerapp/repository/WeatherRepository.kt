package com.breiter.weathercheckerapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.breiter.weathercheckerapp.model.CurrentWeather
import com.breiter.weathercheckerapp.model.Forecast
import com.breiter.weathercheckerapp.model.ForecastItem
import com.breiter.weathercheckerapp.model.WeatherItem
import com.breiter.weathercheckerapp.network.WeatherApi

class WeatherRepository {

    private val _currentWeather = MutableLiveData<CurrentWeather>()
    val currentWeather: LiveData<CurrentWeather>
        get() = _currentWeather

    private val _weathers = Transformations.map(_currentWeather) {
        it.list
    }
    val weathers: LiveData<List<WeatherItem>>
        get() = _weathers

    private val forecast = MutableLiveData<Forecast>()
    private val _forecasts = Transformations.map(forecast) {
        it.list
    }
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
            forecast.value = WeatherApi.retrofitService.getForecast(lat, lon)
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
            forecast.value = WeatherApi.retrofitService.getForecast(cityName)
        } catch (t: Throwable) {
            throw Throwable(t)
        }
    }
}

