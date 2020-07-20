package com.breiter.weathercheckerapp.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.breiter.weathercheckerapp.R


/**
 *  Helper function that converts a given double to
 *  a string, round the number and add the Celsius.
 */
fun Double.asTempString(): String {
    return this.toString().substringBefore(".") + "°C"
}

/**
 * Helper function that match the API weather
 * id with the correct resource drawable id.
 */
fun String.asResourceId(): Int {
    return when (this) {
        "01d" -> R.drawable.icon_01d
        "01n" -> R.drawable.icon_01n
        "02d" -> R.drawable.icon_02d
        "02n" -> R.drawable.icon_02n
        "03d" -> R.drawable.icon_03
        "03n" -> R.drawable.icon_03
        "04d" -> R.drawable.icon_04
        "04n" -> R.drawable.icon_04
        "09d" -> R.drawable.icon_09
        "09n" -> R.drawable.icon_09
        "10d" -> R.drawable.icon_10d
        "10n" -> R.drawable.icon_10n
        "11d" -> R.drawable.icon_11
        "11n" -> R.drawable.icon_11
        "13d" -> R.drawable.icon_13
        "13n" -> R.drawable.icon_13
        "50d" -> R.drawable.icon_50
        "50n" -> R.drawable.icon_50
        else -> R.drawable.icon
    }
}

/**
 * Helper functions to simplify permission checks/requests.
 */
fun Context.hasPermission(permission: String): Boolean {

    // Background permissions didn't exit prior to Q, so it's approved by default.
    if (permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION &&
        android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
        return true
    }

    return ActivityCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
}