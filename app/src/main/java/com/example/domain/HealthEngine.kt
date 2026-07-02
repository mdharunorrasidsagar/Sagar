package com.example.domain

import com.example.data.UserProfileEntity
import kotlin.math.roundToInt

data class HealthCalculations(
    val bmi: Float,
    val bmiCategory: String,
    val bmiMinNormalWeight: Float, // weight for BMI 18.5
    val bmiMaxNormalWeight: Float, // weight for BMI 24.9
    val bmr: Int,                 // Mifflin-St Jeor baseline
    val tdee: Int,                // Total Daily Energy Expenditure (Active Metabolic Rate)
    val targetCalories: Int,      // Adjusted for target goal
    val dailyWaterTargetMl: Int,  // Hydration target in ml
    val macroCarbsGrams: Int,     // 45% of target calories
    val macroProteinGrams: Int,   // 30% of target calories
    val macroFatGrams: Int        // 25% of target calories
)

object HealthEngine {

    fun calculate(profile: UserProfileEntity): HealthCalculations {
        val heightM = profile.heightCm / 100f
        val bmi = if (heightM > 0f) profile.weightKg / (heightM * heightM) else 0f

        val bmiCategory = when {
            bmi < 18.5f -> "Underweight"
            bmi < 25.0f -> "Normal"
            bmi < 30.0f -> "Overweight"
            else -> "Obese"
        }

        val bmiMinNormalWeight = 18.5f * (heightM * heightM)
        val bmiMaxNormalWeight = 24.9f * (heightM * heightM)

        // Mifflin-St Jeor Equation
        val bmrValue = if (profile.gender.equals("Male", ignoreCase = true)) {
            10f * profile.weightKg + 6.25f * profile.heightCm - 5f * profile.age + 5
        } else {
            10f * profile.weightKg + 6.25f * profile.heightCm - 5f * profile.age - 161
        }
        val bmr = bmrValue.roundToInt().coerceAtLeast(1200)

        // TDEE multiplier based on activity level
        val multiplier = when (profile.activityLevel) {
            "Sedentary" -> 1.2f
            "Lightly Active" -> 1.375f
            "Active" -> 1.55f
            "Very Active" -> 1.725f
            else -> 1.2f
        }
        val tdee = (bmr * multiplier).roundToInt()

        // Calorie adjustments for goals
        val targetCalories = when (profile.goalType) {
            "Lose" -> (tdee - 500).coerceAtLeast(1200) // Safe minimum
            "Maintain" -> tdee
            "Gain" -> tdee + 500
            else -> tdee
        }

        // Daily water target is standard 35ml per kg, or safe baseline 2500ml
        val dailyWaterTargetMl = (profile.weightKg * 35).roundToInt().coerceIn(1500, 4000)

        // Macros breakdown: Carbs 45%, Protein 30%, Fat 25%
        val carbsCalories = targetCalories * 0.45f
        val proteinCalories = targetCalories * 0.30f
        val fatCalories = targetCalories * 0.25f

        val carbsGrams = (carbsCalories / 4f).roundToInt()
        val proteinGrams = (proteinCalories / 4f).roundToInt()
        val fatGrams = (fatCalories / 9f).roundToInt()

        return HealthCalculations(
            bmi = (bmi * 10).roundToInt() / 10f,
            bmiCategory = bmiCategory,
            bmiMinNormalWeight = (bmiMinNormalWeight * 10).roundToInt() / 10f,
            bmiMaxNormalWeight = (bmiMaxNormalWeight * 10).roundToInt() / 10f,
            bmr = bmr,
            tdee = tdee,
            targetCalories = targetCalories,
            dailyWaterTargetMl = dailyWaterTargetMl,
            macroCarbsGrams = carbsGrams,
            macroProteinGrams = proteinGrams,
            macroFatGrams = fatGrams
        )
    }
}
