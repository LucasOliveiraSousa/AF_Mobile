package com.example.af_mobile

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.af_mobile.data.repository.WeatherRepository
import com.example.af_mobile.presentation.ui.adapter.WeatherQueryAdapter
import com.example.af_mobile.presentation.viewmodel.HistoryViewModel
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    
    private lateinit var viewModel: HistoryViewModel
    private lateinit var adapter: WeatherQueryAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        
        val repository = WeatherRepository()
        viewModel = HistoryViewModel(repository)
        
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val tvError: TextView = findViewById(R.id.tvError)
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = WeatherQueryAdapter(
            onItemClick = { query ->
                showEditDialog(query)
            },
            onItemLongClick = { query ->
                showDeleteConfirmation(query)
            },
            onFavoriteClick = { query, isFavorite ->
                viewModel.updateFavoriteStatus(query.id, isFavorite)
            }
        )
        recyclerView.adapter = adapter
        
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                if (state.isLoading) {
                    progressBar.visibility = ProgressBar.VISIBLE
                    recyclerView.visibility = RecyclerView.GONE
                    tvError.visibility = TextView.GONE
                } else if (state.error != null) {
                    tvError.text = state.error
                    tvError.visibility = TextView.VISIBLE
                    progressBar.visibility = ProgressBar.GONE
                    recyclerView.visibility = RecyclerView.GONE
                } else {
                    adapter.submitList(state.queries)
                    recyclerView.visibility = RecyclerView.VISIBLE
                    progressBar.visibility = ProgressBar.GONE
                    tvError.visibility = TextView.GONE
                }
            }
        }
    }
    
    private fun showEditDialog(query: com.example.af_mobile.data.model.WeatherQuery) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Observation")
        
        val input = android.widget.EditText(this)
        input.setText(query.observation)
        builder.setView(input)
        
        builder.setPositiveButton("Save") { _, _ ->
            viewModel.updateObservation(query.id, input.text.toString())
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    
    private fun showDeleteConfirmation(query: com.example.af_mobile.data.model.WeatherQuery) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Query")
        builder.setMessage("Are you sure you want to delete this query?")
        builder.setPositiveButton("Delete") { _, _ ->
            viewModel.deleteQuery(query.id)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}
