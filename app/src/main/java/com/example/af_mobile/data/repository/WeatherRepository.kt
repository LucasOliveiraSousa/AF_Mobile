package com.example.af_mobile.data.repository

import com.example.af_mobile.data.model.WeatherQuery
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class WeatherRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val queriesCollection = firestore.collection("weather_queries")
    
    suspend fun saveQuery(query: WeatherQuery): Result<String> {
        return try {
            val docRef = queriesCollection.add(query).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAllQueries(): Result<List<WeatherQuery>> {
        return try {
            val documents = queriesCollection
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            val queries = documents.map { doc ->
                doc.toObject(WeatherQuery::class.java).copy(id = doc.id)
            }
            Result.success(queries)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateQuery(id: String, query: WeatherQuery): Result<Unit> {
        return try {
            queriesCollection.document(id).set(query).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteQuery(id: String): Result<Unit> {
        return try {
            queriesCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateObservation(id: String, observation: String): Result<Unit> {
        return try {
            queriesCollection.document(id).update("observation", observation).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean): Result<Unit> {
        return try {
            queriesCollection.document(id).update("isFavorite", isFavorite).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}