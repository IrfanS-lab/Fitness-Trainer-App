package com.example.fitnesstrainerapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule")
data class ScheduleEntity(
    @PrimaryKey val date: String, // Format: YYYY-MM-DD
    val isMarked: Boolean
)
