package com.example.fitnesstrainerapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calorie_history")
data class CalorieEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val calories: Double,
    val age: Int,
    val height: Double,
    val weight: Double,
    val gender: String,
    val activityLevel: String,
    val timestamp: Long = System.currentTimeMillis()
)
