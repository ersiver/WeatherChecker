package com.breiter.weathercheckerapp.widget.ui

import android.Manifest
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews
import android.widget.Toast
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.breiter.weathercheckerapp.R
import com.breiter.weathercheckerapp.ui.MainActivity
import com.breiter.weathercheckerapp.util.asResourceId
import com.breiter.weathercheckerapp.util.hasPermission
import com.breiter.weathercheckerapp.widget.utils.WIDGET_CITY
import com.breiter.weathercheckerapp.widget.utils.WIDGET_DESCR
import com.breiter.weathercheckerapp.widget.utils.WIDGET_ICON
import com.breiter.weathercheckerapp.widget.utils.WIDGET_PREF
import com.breiter.weathercheckerapp.widget.utils.WIDGET_TEMP
import com.breiter.weathercheckerapp.widget.work.WidgetUpdateWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Implementation of App Widget functionality.
 */
class WeatherAppWidget : AppWidgetProvider() {
    /**
     * Override for onUpdate() method, to handle
     * all widget update implicit requests.
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds)
            updateAppWidget(
                context,
                appWidgetManager,
                appWidgetId
            )
    }

    companion object {

        /**
         * Update a single app widget. This is a helper
         * method that handles one widget update at a time.
         */
        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val sharedPref: SharedPreferences = context.getSharedPreferences(
                WIDGET_PREF, MODE_PRIVATE
            )
            // Get the weather data from prefs.
            val icon: String =
                sharedPref.getString(WIDGET_ICON, context.getString(R.string.widget_default))
                    .toString()
            val temp: String =
                sharedPref.getString(WIDGET_TEMP, context.getString(R.string.default_null))
                    .toString()
            val descr: String =
                sharedPref.getString(WIDGET_DESCR, context.getString(R.string.default_null))
                    .toString()
            val city: String =
                sharedPref.getString(WIDGET_CITY, context.getString(R.string.default_null))
                    .toString()

            // Construct the RemoteViews object.
            val views = RemoteViews(
                context.packageName,
                R.layout.app_widget
            )

            views.apply {
                setImageViewResource(R.id.weather_icon, icon.asResourceId())
                setTextViewText(R.id.appwidget_temp, temp)
                setTextViewText(R.id.appwidget_city, city)
                setTextViewText(R.id.appwidget_description, descr.capitalize())
            }

            //Tapping on the widget launches the app and opens MainActivity.
            val intent = Intent(context, MainActivity::class.java)

            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        /**
         * Invoked by WorkManager, when location is updated.
         */
        fun notifyAppWidgetViewDataChanged(context: Context) {
            val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(
                    context,
                    WeatherAppWidget::class.java
                )
            )
            for (appWidgetId in appWidgetIds)
                updateAppWidget(
                    context,
                    appWidgetManager,
                    appWidgetId
                )
        }
    }

    /**
     * When the first AppWidget instance is attached to the screen,
     * call WorkManager to schedule updating the widget info.
     */
    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        if (context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            context.hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        )
            setupRecurringWork(context)
         else
            Toast.makeText(
                context,
                R.string.location_required_explanation_widget,
                Toast.LENGTH_LONG
            ).show()
    }

    /**
     * Setup WorkManager background job to get
     * current device's location abd the weather
     * for that location. The first execution
     * happens immediately and then every 15 minutes.
     */
    private fun setupRecurringWork(context: Context) {
        val repeatingRequest =
            PeriodicWorkRequestBuilder<WidgetUpdateWorker>(15, TimeUnit.MINUTES)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WidgetUpdateWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }

    /**
     * When the last AppWidget instance for this
     * provider is deleted all updates scheduled
     * by WorkManager are cancelled.
     */
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WorkManager.getInstance(context)
            .cancelUniqueWork(WidgetUpdateWorker.WORK_NAME)

        Timber.i("Work request cancelled")
    }
}