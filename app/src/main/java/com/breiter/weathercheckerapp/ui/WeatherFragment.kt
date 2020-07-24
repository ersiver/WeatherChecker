package com.breiter.weathercheckerapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.breiter.weathercheckerapp.BuildConfig
import com.breiter.weathercheckerapp.R
import com.breiter.weathercheckerapp.databinding.WeatherFragmentBinding
import com.breiter.weathercheckerapp.viewmodel.WeatherViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.TimeUnit

class WeatherFragment : Fragment() {
    private val binding: WeatherFragmentBinding by lazy {
        WeatherFragmentBinding.inflate(layoutInflater)
    }

    private val weatherViewModel: WeatherViewModel by lazy {
        ViewModelProvider(this).get(WeatherViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = weatherViewModel
            forecastList.adapter = ForecastAdapter()
        }

        requestLastLocationOrStartLocationUpdates()
        setWindowInsets()

        return binding.root
    }

    /**
     * Find the system inset value for the top and bottom
     * and increase the view's padding by that amount to
     * prevent overlapping.
     */
    private fun setWindowInsets() {
        val weatherLayout: ConstraintLayout = binding.weatherLayout
        weatherLayout.setOnApplyWindowInsetsListener { view, insets ->
            view.updatePadding(
                top = insets.systemWindowInsetTop,
                bottom = insets.systemWindowInsetBottom
            )
            insets
        }
    }

    /**
     * Location operations.
     *
     * Checks for permissions, and requests them if they aren't present.
     * If they are, requests  the last location of this device,
     * if known, otherwise start periodic location updates.
     */
    private fun requestLastLocationOrStartLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        } else {
            //Permission granted
            val fusedLocationClient = LocationServices
                .getFusedLocationProviderClient(requireContext())

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null)
                    weatherViewModel.onLocationUpdated(location)
                else
                //Subscribe to location changes.
                fusedLocationClient.requestLocationUpdates(
                        getLocationRequest(),
                        getLocationCallback(),
                        Looper.getMainLooper()
                    )
            }
        }
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
                weatherViewModel.onLocationUpdated(location)
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
     * Show the user a dialog asking for permission
     * to use location. If the device is running
     * Android Q (API 29) or higher,permission to
     * access location in the background is also
     * needed since the widget will be update even,
     * when the app is in background.
     */
    private fun requestLocationPermission() {
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        var requestCode = REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
            requestCode = REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_REQUEST_CODE
        }
        requestPermissions(permissionsArray, requestCode)
    }

    /**
     * This will be called, when the user responds to the permission request.
     * If granted, continue with the operation that the user gave us permission to do.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (
            grantResults.isEmpty() ||
            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
            (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_REQUEST_CODE &&
                    grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                    PackageManager.PERMISSION_DENIED)
        ) {
            // Permission denied, display Snackbar with action to enable location.
            Snackbar.make(
                binding.weatherLayout,
                R.string.location_required_explanation,
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Settings") {
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                startActivity(intent)
            }.show()
            return
        } else

        // Permission granted
        requestLastLocationOrStartLocationUpdates()
    }

    companion object {
        private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_REQUEST_CODE = 33
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        private const val LOCATION_PERMISSION_INDEX = 0
        private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
    }
}