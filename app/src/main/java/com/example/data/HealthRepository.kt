package com.example.data

import kotlinx.coroutines.flow.Flow

class HealthRepository(private val healthDao: HealthDao) {
    val userProfile: Flow<UserProfileEntity?> = healthDao.getUserProfile()
    val allWaterLogs: Flow<List<WaterLogEntity>> = healthDao.getAllWaterLogs()
    val allWeightLogs: Flow<List<WeightLogEntity>> = healthDao.getAllWeightLogs()

    fun getWaterLogsByDate(dateStr: String): Flow<List<WaterLogEntity>> {
        return healthDao.getWaterLogsByDate(dateStr)
    }

    suspend fun insertUserProfile(profile: UserProfileEntity) {
        healthDao.insertUserProfile(profile)
    }

    suspend fun insertWaterLog(log: WaterLogEntity) {
        healthDao.insertWaterLog(log)
    }

    suspend fun deleteWaterLog(id: Int) {
        healthDao.deleteWaterLogById(id)
    }

    suspend fun insertWeightLog(log: WeightLogEntity) {
        healthDao.insertWeightLog(log)
    }

    suspend fun deleteWeightLog(id: Int) {
        healthDao.deleteWeightLogById(id)
    }
}
