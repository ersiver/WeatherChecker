package com.breiter.weathercheckerapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.breiter.weathercheckerapp.R
import com.breiter.weathercheckerapp.domain.ForecastItem
import com.breiter.weathercheckerapp.domain.WeatherItem
import com.breiter.weathercheckerapp.ui.ForecastAdapter
import com.breiter.weathercheckerapp.viewmodel.WeatherApiStatus
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("hideSoftInputFromWindow")
fun dismissKeyboard(view: View, isTypingComplete: Boolean) {
    val inputMethodManager =
        view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    if (isTypingComplete)
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

}

@BindingAdapter("toastWarningMessage")
fun displayToastWarning(view: View, isEmpty: Boolean) {
    if (isEmpty) Toast.makeText(view.context, R.string.warning_message, Toast.LENGTH_SHORT).show()
}

@BindingAdapter("visibility")
fun bindResultsOnApiStatus(
    view: View,
    status: WeatherApiStatus?
) {

    when (status) {

        WeatherApiStatus.START -> {
            view.visibility = View.GONE
        }
        WeatherApiStatus.LOADING -> {
            view.visibility = View.GONE
        }
        WeatherApiStatus.ERROR -> {
            view.visibility = View.GONE
        }
        WeatherApiStatus.DONE -> {
            view.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("temperatureFormatted")
fun TextView.setTemperatureFormatted(temp: Double) {
    //val tempAsString = temp.toString().substringBefore(".") + "°C"
    text = temp.asTempString()
}

@BindingAdapter("weatherIcon")
fun ImageView.setIcon(data: List<WeatherItem>?) {
    var resId = R.drawable.icon
    data?.let {
        val item = data[0]
        resId = item.iconId.asResourceId()
    }
    setImageResource(resId)
}

@BindingAdapter("dayOfWeek")
fun TextView.setDayOfWeek(dt: Long) {
    val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
    val dayOfWeek = sdf.format(getDateFromTimeStamp(dt))
    text = dayOfWeek
}

@BindingAdapter("dateFormatted")
fun TextView.setDateFormatted(dt: Long) {
    val sdf = SimpleDateFormat("d MMMM", Locale.getDefault())
    text = sdf.format(getDateFromTimeStamp(dt))
}

@BindingAdapter("dayOfWeekShort")
fun TextView.setDayOfWeekShort(dt: Long) {
    val sdf = SimpleDateFormat("EEE", Locale.getDefault())
    text = sdf.format(getDateFromTimeStamp(dt))
}

@BindingAdapter("dateShortFormatted")
fun TextView.setDateShortFormatted(dt: Long) {
    val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
    text = sdf.format(getDateFromTimeStamp(dt))
}

private fun getDateFromTimeStamp(time: Long): Date {
    val mili = 1000L
    val timestamp = Timestamp(time * mili)
    return Date(timestamp.time)
}

@SuppressLint("DefaultLocale")
@BindingAdapter("descriptionFormatted")
fun TextView.setDescription(data: List<WeatherItem>?) {
    val item = data?.first()
    text = item?.description?.capitalize()
}

@BindingAdapter("humidityFormatted")
fun TextView.setHumidityFormatted(humidity: Double) {
    val humidityAsString = humidity.toString().substringBefore(".") + "%"
    text = humidityAsString
}

@BindingAdapter("pressureFormatted")
fun TextView.setPressureFormatted(pressure: Double) {
    val pressureAsString = pressure.toString().substringBefore(".") + " hPa"
    text = pressureAsString
}

@BindingAdapter("windSpeedFormatted")
fun TextView.setWindSpeed(wind: Double) {
    val windAsString = wind.toString().substringBefore(".") + " m/s"
    text = windAsString
}

@BindingAdapter("forecastListData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<ForecastItem>?) {
    val adapter = recyclerView.adapter as ForecastAdapter
    adapter.submitList(data)
}

@BindingAdapter("visibleOnError")
fun bindStatus(
    statusImageView: ImageView,
    status: WeatherApiStatus?
) {
    when (status) {
        WeatherApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        WeatherApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_error)
        }
        else -> statusImageView.visibility = View.GONE
    }
}


@BindingAdapter("visibleOnError")
fun bindResultOnApiStatus(
    view: View,
    status: WeatherApiStatus?
) {

    when (status) {

        WeatherApiStatus.START -> {
            view.visibility = View.GONE
        }
        WeatherApiStatus.LOADING -> {
            view.visibility = View.GONE
        }
        WeatherApiStatus.ERROR -> {
            view.visibility = View.VISIBLE
        }
        WeatherApiStatus.DONE -> {
            view.visibility = View.GONE
        }
    }
}

@BindingAdapter("errorText")
fun setErrorText(textView: TextView, errorMessage: String?) {
    var message = textView.context.getString(R.string.something_wrong_error)

    errorMessage?.let {
        if (isServerError(it))
            message = textView.context.getString(R.string.wrong_input_error)
        else if (isUnableResolveHost(it))
            message = textView.context.getString(R.string.no_internet_error)
    }
    textView.text = message
}

fun isServerError(errorMessage: String) = errorMessage.contains("Internal Server Error")

fun isUnableResolveHost(errorMessage: String) = errorMessage.contains("Unable to resolve host")




