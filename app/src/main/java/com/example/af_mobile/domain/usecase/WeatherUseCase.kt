package com.example.af_mobile.domain.usecase

import com.example.af_mobile.data.api.ApiClient
import com.example.af_mobile.data.model.WeatherResponse

class WeatherUseCase {
    private val apiService = ApiClient.getWeatherApiService()
    
    suspend fun fetchWeather(latitude: Double, longitude: Double): Result<WeatherResponse> {
        return try {
            val response = apiService.getCurrentWeather(latitude, longitude)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}