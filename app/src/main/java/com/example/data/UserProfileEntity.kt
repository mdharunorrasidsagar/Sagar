package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1, // Single user profile
    val name: String,
    val age: Int,
    val gender: String, // "Male" or "Female"
    val heightCm: Float,
    val weightKg: Float,
    val activityLevel: String, // "Sedentary", "Lightly Active", "Active", "Very Active"
    val goalType: String, // "Lose", "Maintain", "Gain"
    val isOnboardingCompleted: Boolean = false
)
