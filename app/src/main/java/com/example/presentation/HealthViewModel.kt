package com.example.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.domain.HealthCalculations
import com.example.domain.HealthEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HealthViewModel(application: Application, private val repository: HealthRepository) : AndroidViewModel(application) {

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allWeightLogs: StateFlow<List<WeightLogEntity>> = repository.allWeightLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentDate = MutableStateFlow(getCurrentDateString())
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    val todayWaterLogs: StateFlow<List<WaterLogEntity>> = _currentDate
        .flatMapLatest { date -> repository.getWaterLogsByDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val healthCalculations: StateFlow<HealthCalculations?> = userProfile
        .map { profile -> profile?.let { HealthEngine.calculate(it) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        // Seed default weight logs if empty so that the user gets an immediately beautiful, animated history trend line chart!
        viewModelScope.launch {
            repository.allWeightLogs.first().let { logs ->
                if (logs.isEmpty()) {
                    // Seed past 7 days of weight logs slightly fluctuating
                    val calendar = Calendar.getInstance()
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val baselineWeight = 72.5f
                    for (i in 6 downTo 0) {
                        val seedCal = calendar.clone() as Calendar
                        seedCal.add(Calendar.DAY_OF_YEAR, -i)
                        val dateStr = sdf.format(seedCal.time)
                        val weight = baselineWeight + (Math.sin(i.toDouble()) * 0.8f).toFloat()
                        repository.insertWeightLog(
                            WeightLogEntity(
                                dateStr = dateStr,
                                weightKg = (weight * 10).toInt() / 10f,
                                timestamp = seedCal.timeInMillis
                            )
                        )
                    }
                }
            }
        }
    }

    fun completeOnboarding(
        name: String,
        age: Int,
        gender: String,
        height: Float,
        weight: Float,
        activityLevel: String,
        goalType: String
    ) {
        viewModelScope.launch {
            val profile = UserProfileEntity(
                name = name,
                age = age,
                gender = gender,
                heightCm = height,
                weightKg = weight,
                activityLevel = activityLevel,
                goalType = goalType,
                isOnboardingCompleted = true
            )
            repository.insertUserProfile(profile)

            // Log current weight to weight history logs
            val todayStr = getCurrentDateString()
            repository.insertWeightLog(
                WeightLogEntity(
                    dateStr = todayStr,
                    weightKg = weight,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.insertUserProfile(profile)
            // Log weight to history logs
            repository.insertWeightLog(
                WeightLogEntity(
                    dateStr = getCurrentDateString(),
                    weightKg = profile.weightKg,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun addWaterLog(amountMl: Int) {
        viewModelScope.launch {
            repository.insertWaterLog(
                WaterLogEntity(
                    dateStr = getCurrentDateString(),
                    amountMl = amountMl
                )
            )
        }
    }

    fun removeWaterLog(id: Int) {
        viewModelScope.launch {
            repository.deleteWaterLog(id)
        }
    }

    fun logWeight(weightKg: Float) {
        viewModelScope.launch {
            repository.insertWeightLog(
                WeightLogEntity(
                    dateStr = getCurrentDateString(),
                    weightKg = weightKg
                )
            )
            // Also update weight in user profile so calculations stay updated!
            userProfile.value?.let { currentProfile ->
                repository.insertUserProfile(currentProfile.copy(weightKg = weightKg))
            }
        }
    }

    fun deleteWeightLog(id: Int) {
        viewModelScope.launch {
            repository.deleteWeightLog(id)
        }
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    // Factory
    class Factory(private val application: Application, private val repository: HealthRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HealthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HealthViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
