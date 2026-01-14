package com.example.fitnesstrainerapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_history")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val progress: Float,
    val imageRes: Int,
    val timestamp: Long = System.currentTimeMillis() // To show newest first
)