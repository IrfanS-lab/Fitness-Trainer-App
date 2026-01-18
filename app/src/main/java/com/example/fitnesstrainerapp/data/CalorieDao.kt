package com.example.fitnesstrainerapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalorieDao {
    @Query("SELECT * FROM calorie_history ORDER BY timestamp DESC")
    fun getCalorieHistory(): Flow<List<CalorieEntity>>

    @Insert
    suspend fun insertCalorie(calorie: CalorieEntity)
}
