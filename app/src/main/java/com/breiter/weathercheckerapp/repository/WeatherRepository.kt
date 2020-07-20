package com.breiter.weathercheckerapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.breiter.weathercheckerapp.domain.CurrentWeather
import com.breiter.weathercheckerapp.domain.ForecastItem
import com.breiter.weathercheckerapp.network.CurrentWeatherDTO
import com.breiter.weathercheckerapp.network.WeatherApi
import com.breiter.weathercheckerapp.network.asDomainModel
import timber.log.Timber

class WeatherRepository {

    private val _currentWeather = MutableLiveData<CurrentWeatherDTO>()
    val currentWeather: LiveData<CurrentWeather> =
        Transformations.map(_currentWeather) {
            it.asDomainModel()
        }

    private val _forecasts = MutableLiveData<List<ForecastItem>>()
    val forecasts: LiveData<List<ForecastItem>>
        get() = _forecasts

    private var _widgetWeather: CurrentWeather? = null
    val widgetWeather: CurrentWeather?
        get() = _widgetWeather


    /**
     * Gets weather information for current location from the
     * Weather API service and updates the _currentWeather
     * and _forecasts LiveData. Retrofit makes suspending
     * functions main-safe.
     */
    suspend fun getWeatherAndForecasts(lat: Double, lon: Double) {
        try {
            _currentWeather.value = WeatherApi.retrofitService.getCurrentWeather(lat, lon)
            val forecastResponse = WeatherApi.retrofitService.getForecast(lat, lon)
            _forecasts.value = forecastResponse.asDomainModel()
        } catch (t: Throwable) {
            throw Throwable(t)
        }
    }

    /**
     * Gets weather information for user's input from Weather API Retrofit
     * service and updates the _currentWeather and _forecasts LiveData.
     */
    suspend fun getWeatherAndForecasts(cityName: String) {
        try {
            _currentWeather.value = WeatherApi.retrofitService.getCurrentWeather(cityName)
            val forecastResponse = WeatherApi.retrofitService.getForecast(cityName)
            _forecasts.value = forecastResponse.asDomainModel()
        } catch (t: Throwable) {
            throw Throwable(t)
        }
    }

    /**
     * Gets weather information for current location.
     * This is to update the widget when app is in background.
     */
    suspend fun getWeather(lat: Double, lon: Double) {
        try {
            _widgetWeather = WeatherApi.retrofitService.getCurrentWeather(lat, lon).asDomainModel()
        } catch (t: Throwable) {
            Timber.d(t)
        }
    }
}