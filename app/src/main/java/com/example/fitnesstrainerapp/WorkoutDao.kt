package com.example.fitnesstrainerapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_history ORDER BY timestamp DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Insert
    suspend fun insertWorkout(workout: WorkoutEntity)
}