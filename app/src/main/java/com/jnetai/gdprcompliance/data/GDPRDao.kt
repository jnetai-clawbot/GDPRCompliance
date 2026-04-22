package com.jnetai.gdprcompliance.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface GDPRDao {
    @Query("SELECT * FROM processing_activities ORDER BY dateAdded DESC")
    fun getAllActivities(): Flow<List<ProcessingActivity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ProcessingActivity): Long

    @Query("SELECT * FROM processing_activities WHERE id = :id")
    suspend fun getActivity(id: Long): ProcessingActivity

    @Delete
    suspend fun deleteActivity(activity: ProcessingActivity)

    @Query("SELECT * FROM compliance_checks ORDER BY article ASC")
    fun getAllChecks(): Flow<List<ComplianceCheck>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheck(check: ComplianceCheck): Long

    @Update
    suspend fun updateCheck(check: ComplianceCheck)

    @Query("SELECT * FROM risk_assessments ORDER BY dateAssessed DESC")
    fun getAllRisks(): Flow<List<RiskAssessment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRisk(risk: RiskAssessment): Long

    @Query("SELECT * FROM risk_assessments WHERE activityId = :activityId")
    suspend fun getRisksForActivity(activityId: Long): List<RiskAssessment>

    @Query("SELECT * FROM breach_logs ORDER BY dateOccurred DESC")
    fun getAllBreaches(): Flow<List<BreachLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreach(breach: BreachLog): Long

    @Update
    suspend fun updateBreach(breach: BreachLog)
}