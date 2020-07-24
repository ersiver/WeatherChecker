package com.breiter.weathercheckerapp.widget.work

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Looper
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.breiter.weathercheckerapp.domain.CurrentWeather
import com.breiter.weathercheckerapp.repository.WeatherRepository
import com.breiter.weathercheckerapp.util.asTempString
import com.breiter.weathercheckerapp.widget.ui.WeatherAppWidget
import com.breiter.weathercheckerapp.widget.utils.*
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

class WidgetUpdateWorker(private val context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    //The data source this Worker will fetch weather from.
    private val repository = WeatherRepository()

    //Scope and job for all coroutines launched by this worker.
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    // Location classes.
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override suspend fun doWork(): Result {
        try {
            Timber.d("Work request is run")
            startLocationUpdates()
        } catch (e: Exception) {
            Timber.d("Work request failed: ${e.message}")
            return Result.retry()
        }
        return Result.success()
    }

    /**
     * Start location updates, this will ask the
     * operating system to get the device's location.
     *
     * Permissions are handled in the WeatherFragment.
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {

        // Initialize the FusedLocationClient.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // Initialize the location callbacks.
        locationCallback = getLocationCallback()

        //Subscribe to location changes.
        fusedLocationClient.requestLocationUpdates(
            getLocationRequest(),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    /**
     * The callback that is triggered when the
     * FusedLocationClient updates the device's location.
     */
    private fun getLocationCallback(): LocationCallback {
        return object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                val location = locationResult?.lastLocation ?: return
                updateWeather(location)
            }
        }
    }

    /**
     * Sets up the location request.
     *
     * @return The LocationRequest object containing the desired parameters.
     */
    private fun getLocationRequest(): LocationRequest {
        return LocationRequest().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    /**
     * Request weather for the current location.
     */
    private fun updateWeather(location: Location) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                repository.getWeather(location.latitude, location.longitude)
                val currentWeather = repository.widgetWeather
                currentWeather?.let { weather ->
                    savedWeatherToSharedPrefsAndNotifyWidget(weather)
                }
            }
        }
    }

    /**
     * Notify the widget on location update.
     */
    private fun savedWeatherToSharedPrefsAndNotifyWidget(weather: CurrentWeather) {
        val sharedPref = context
            .getSharedPreferences(WIDGET_PREF, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(WIDGET_ICON, weather.weathers[0].iconId)
        editor.putString(WIDGET_CITY, weather.city)
        editor.putString(WIDGET_DESCR, weather.weathers[0].description)
        editor.putString(WIDGET_TEMP, weather.temp.asTempString())
        editor.apply()
        WeatherAppWidget.notifyAppWidgetViewDataChanged(context)
    }

    companion object {
        //Define a work name to uniquely identify this worker.
        const val WORK_NAME = "com.breiter.weathercheckerapp.WidgetUpdateWorker"
    }
}