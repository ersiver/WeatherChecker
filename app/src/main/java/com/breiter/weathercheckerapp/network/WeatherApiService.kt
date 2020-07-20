package com.breiter.weathercheckerapp.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://openweathermap.org/data/2.5/"
private const val API_KEY = "appid=439d4b804bc8187953eb36d2a8c26a02" //This is a sample key.

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        retrofit.create(
            WeatherApiService::class.java
        )
    }
}

interface WeatherApiService {

    @GET("weather?$API_KEY")
    suspend fun getCurrentWeather(@Query("q") city: String): CurrentWeatherDTO

    @GET("weather?$API_KEY")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double, @Query("lon") lon: Double
    ): CurrentWeatherDTO

    @GET("forecast/daily?$API_KEY")
    suspend fun getForecast(@Query("q") city: String): ForecastDTO

    @GET("forecast/daily?$API_KEY")
    suspend fun getForecast(
        @Query("lat") lat: Double, @Query("lon") lon: Double
    ): ForecastDTO
}


