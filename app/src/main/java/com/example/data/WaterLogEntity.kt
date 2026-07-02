package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_logs")
data class WaterLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateStr: String, // "yyyy-MM-dd"
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis()
)
