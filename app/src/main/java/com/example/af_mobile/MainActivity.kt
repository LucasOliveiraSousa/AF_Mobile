package com.example.af_mobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ProgressBar
import android.widget.EditText
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.af_mobile.data.model.WeatherQuery
import com.example.af_mobile.data.repository.WeatherRepository
import com.example.af_mobile.domain.usecase.LocationUseCase
import com.example.af_mobile.domain.usecase.WeatherUseCase
import com.example.af_mobile.presentation.viewmodel.WeatherViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var viewModel: WeatherViewModel
    private val PERMISSION_REQUEST_CODE = 100
    
    override fun onCreate(savedInstanceState: Bundle?) {\n        super.onCreate(savedInstanceState)\n        setContentView(R.layout.activity_main)\n        \n        val locationUseCase = LocationUseCase(this)\n        val weatherUseCase = WeatherUseCase()\n        val repository = WeatherRepository()\n        viewModel = WeatherViewModel(locationUseCase, weatherUseCase, repository)\n        \n        val btnGetWeather: Button = findViewById(R.id.btnGetWeather)\n        val btnSave: Button = findViewById(R.id.btnSave)\n        val btnHistory: Button = findViewById(R.id.btnHistory)\n        val contentLayout: LinearLayout = findViewById(R.id.contentLayout)\n        val progressBar: ProgressBar = findViewById(R.id.progressBar)\n        val tvError: TextView = findViewById(R.id.tvError)\n        val tvTemperature: TextView = findViewById(R.id.tvTemperature)\n        val tvCoordinates: TextView = findViewById(R.id.tvCoordinates)\n        val tvFeelsLike: TextView = findViewById(R.id.tvFeelsLike)\n        val tvWeatherCondition: TextView = findViewById(R.id.tvWeatherCondition)\n        val tvWindSpeed: TextView = findViewById(R.id.tvWindSpeed)\n        val etObservation: EditText = findViewById(R.id.etObservation)\n        val cbFavorite: CheckBox = findViewById(R.id.cbFavorite)\n        \n        btnGetWeather.setOnClickListener {\n            if (checkLocationPermission()) {\n                viewModel.fetchWeatherByLocation()\n            } else {\n                requestLocationPermission()\n            }\n        }\n        \n        lifecycleScope.launch {\n            viewModel.uiState.collect { state ->\n                if (state.isLoading) {\n                    progressBar.visibility = ProgressBar.VISIBLE\n                    contentLayout.visibility = LinearLayout.GONE\n                    tvError.visibility = TextView.GONE\n                } else if (state.error != null) {\n                    tvError.text = state.error\n                    tvError.visibility = TextView.VISIBLE\n                    progressBar.visibility = ProgressBar.GONE\n                    contentLayout.visibility = LinearLayout.GONE\n                } else if (state.query != null) {\n                    tvTemperature.text = \"Temperature: ${state.query.temperature}°C\"\n                    tvCoordinates.text = \"Location: ${state.query.latitude}, ${state.query.longitude}\"\n                    tvFeelsLike.text = \"Feels like: ${state.query.feelsLike}°C\"\n                    tvWeatherCondition.text = \"Condition: ${state.query.weatherCondition}\"\n                    tvWindSpeed.text = \"Wind: ${state.query.windSpeed} km/h\"\n                    contentLayout.visibility = LinearLayout.VISIBLE\n                    progressBar.visibility = ProgressBar.GONE\n                    tvError.visibility = TextView.GONE\n                }\n            }\n        }\n        \n        btnSave.setOnClickListener {\n            val query = viewModel.uiState.value.query ?: return@setOnClickListener\n            val observation = etObservation.text.toString()\n            val isFavorite = cbFavorite.isChecked\n            val newQuery = query.copy(\n                observation = observation,\n                isFavorite = isFavorite,\n                timestamp = Timestamp.now()\n            )\n            viewModel.saveQuery(newQuery)\n        }\n        \n        btnHistory.setOnClickListener {\n            startActivity(Intent(this, HistoryActivity::class.java))\n        }\n    }\n    \n    private fun checkLocationPermission(): Boolean {\n        return ContextCompat.checkSelfPermission(\n            this,\n            Manifest.permission.ACCESS_FINE_LOCATION\n        ) == PackageManager.PERMISSION_GRANTED\n    }\n    \n    private fun requestLocationPermission() {\n        ActivityCompat.requestPermissions(\n            this,\n            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),\n            PERMISSION_REQUEST_CODE\n        )\n    }\n    \n    override fun onRequestPermissionsResult(\n        requestCode: Int,\n        permissions: Array<out String>,\n        grantResults: IntArray\n    ) {\n        super.onRequestPermissionsResult(requestCode, permissions, grantResults)\n        if (requestCode == PERMISSION_REQUEST_CODE) {\n            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {\n                viewModel.fetchWeatherByLocation()\n            }\n        }\n    }\n}
