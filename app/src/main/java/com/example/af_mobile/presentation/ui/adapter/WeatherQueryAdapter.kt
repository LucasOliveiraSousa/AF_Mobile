package com.example.af_mobile.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.af_mobile.data.model.WeatherQuery
import com.example.af_mobile.databinding.ItemWeatherQueryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherQueryAdapter(
    private val onItemClick: (WeatherQuery) -> Unit,
    private val onItemLongClick: (WeatherQuery) -> Unit,
    private val onFavoriteClick: (WeatherQuery, Boolean) -> Unit
) : RecyclerView.Adapter<WeatherQueryAdapter.ViewHolder>() {
    
    private var queries: List<WeatherQuery> = emptyList()
    
    fun submitList(newQueries: List<WeatherQuery>) {
        queries = newQueries
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWeatherQueryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(queries[position])
    }
    
    override fun getItemCount(): Int = queries.size
    
    inner class ViewHolder(private val binding: ItemWeatherQueryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(query: WeatherQuery) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dateStr = dateFormat.format(Date(query.timestamp.seconds * 1000))
            
            binding.apply {
                tvDateTime.text = dateStr
                tvTemperature.text = "${query.temperature}°C"
                tvWeatherCondition.text = query.weatherCondition
                tvCoordinates.text = "${query.latitude}, ${query.longitude}"
                tvObservation.text = query.observation.takeIf { it.isNotEmpty() } ?: "No observation"
                ibFavorite.isActivated = query.isFavorite
                
                root.setOnClickListener {
                    onItemClick(query)
                }
                
                root.setOnLongClickListener {
                    onItemLongClick(query)
                    true
                }
                
                ibFavorite.setOnClickListener {
                    onFavoriteClick(query, !query.isFavorite)
                }
            }
        }
    }
}
