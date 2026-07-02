package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_logs")
data class WeightLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateStr: String, // "yyyy-MM-dd"
    val weightKg: Float,
    val timestamp: Long = System.currentTimeMillis()
)
