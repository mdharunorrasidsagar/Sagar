package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM water_logs ORDER BY timestamp DESC")
    fun getAllWaterLogs(): Flow<List<WaterLogEntity>>

    @Query("SELECT * FROM water_logs WHERE dateStr = :dateStr ORDER BY timestamp DESC")
    fun getWaterLogsByDate(dateStr: String): Flow<List<WaterLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(log: WaterLogEntity)

    @Query("DELETE FROM water_logs WHERE id = :id")
    suspend fun deleteWaterLogById(id: Int)

    @Query("SELECT * FROM weight_logs ORDER BY timestamp ASC")
    fun getAllWeightLogs(): Flow<List<WeightLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLog(log: WeightLogEntity)

    @Query("DELETE FROM weight_logs WHERE id = :id")
    suspend fun deleteWeightLogById(id: Int)
}
