
package com.breiter.weathercheckerapp.viewmodel

import android.location.Location
import android.text.Editable
import androidx.lifecycle.*
import com.breiter.weathercheckerapp.domain.CurrentWeather
import com.breiter.weathercheckerapp.domain.ForecastItem
import com.breiter.weathercheckerapp.repository.WeatherRepository
import kotlinx.coroutines.launch

enum class WeatherApiStatus { START, LOADING, ERROR, DONE }

class WeatherViewModel : ViewModel() {
    private val _currentLocation = MutableLiveData<Location>()

    private val repository: WeatherRepository = WeatherRepository()

    private val query = MutableLiveData<String>()

    private val _currentWeather = repository.currentWeather
    val currentWeather: LiveData<CurrentWeather>
        get() = _currentWeather

    private val _forecasts = repository.forecasts
    val forecasts: LiveData<List<ForecastItem>>
        get() = _forecasts

    private val _status = MutableLiveData<WeatherApiStatus>()
    val status: LiveData<WeatherApiStatus>
        get() = _status

    private val _resultForCurrentLocation = MutableLiveData<Boolean>()
    val resultForCurrentLocation: LiveData<Boolean>
        get() = _resultForCurrentLocation

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    private val _toastWarningEvent = MutableLiveData<Boolean?>()
    val toastWarningEvent: LiveData<Boolean?>
        get() = _toastWarningEvent

    private val _typingCompleteEvent = MutableLiveData<Boolean?>()
    val typingCompleteEvent: LiveData<Boolean?>
        get() = _typingCompleteEvent

    init {
        query.value = ""
        _status.value = WeatherApiStatus.START
    }

    /**
     * Invoked immediately when the operating
     * system gets the devices location.
     */
    fun onLocationUpdated(location: Location) {
        _currentLocation.value = location
        getWeatherDataForCurrentLocation()
    }

    /**
     * Refresh _currentWeather and _forecasts LiveData via repository.
     * Sets value of _resultForCurrentLocation as true to display pin location image.
     */
    private fun getWeatherDataForCurrentLocation() = launchDataLoad {
        _currentLocation.value?.let { it ->
            repository.getWeatherAndForecasts(it.latitude, it.longitude)
            _resultForCurrentLocation.value = true
        }
    }

    //Executes afterTextChanged and sets value of query LiveData to user's input.
    fun afterCityTextChange(e: Editable) {
        query.value = e.trim().toString()
    }

    /**
     * Executes when the button GO is clicked.
     * Calls function to update weather data for user's input.
     * Displays Toast warning, if the editText is empty.
     */
    fun onStartSearching() {
        _typingCompleteEvent.value = true

        query.value?.let {
            if (it.isBlank())
                _toastWarningEvent.value = true
            else
                getWeatherDataForCity(it)
        }
    }

    /**
     * Refresh _currentWeather and _forecasts LiveData via repository.
     * Sets value of _resultForCurrentLocation to false to hide a pin location image.
     */
    private fun getWeatherDataForCity(cityName: String) = launchDataLoad {
        _resultForCurrentLocation.value = false
        repository.getWeatherAndForecasts(cityName)
    }

    /**
     * Data loading coroutine gets data via repository.
     * Updates status LiveData and error message on fail.
     * A suspend lambda allows to call suspend functions.
     * The library adds a viewModelScope as an extension function of the ViewModel.
     * The scope is bound to Dispatchers.Main and will automatically be cancelled onCleared().
     */
    private fun launchDataLoad(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                _status.value = WeatherApiStatus.LOADING
                block()
                _status.value = WeatherApiStatus.DONE
            } catch (error: Throwable) {
                _errorMessage.value = error.message
                _status.value = WeatherApiStatus.ERROR
            }
        }
    }
}