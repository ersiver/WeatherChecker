package com.breiter.weathercheckerapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.breiter.weathercheckerapp.databinding.WeatherFragmentBinding
import com.breiter.weathercheckerapp.viewmodel.WeatherViewModel

class WeatherFragment : Fragment() {
    private lateinit var weatherViewModel: WeatherViewModel

    companion object {
        const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = WeatherFragmentBinding.inflate(layoutInflater)

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = weatherViewModel
            forecastList.adapter = ForecastAdapter()
        }

        startLocationServices()

        return binding.root
    }

    /**
     * Verifying permission for location. When permission is granted
     *  viewModel handles listening for current location.
     */
    private fun startLocationServices() {
        val permission = context?.let {
            ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), LOCATION_REQUEST_CODE
            )
        } else {
            weatherViewModel.startListening()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    weatherViewModel.startListening()
                }
                return
            }
        }
    }

}



