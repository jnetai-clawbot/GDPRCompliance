package com.jnetai.gdprcompliance.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "processing_activities")
data class ProcessingActivity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val legalBasis: String,
    val dataCategories: String,
    val dateAdded: LocalDate = LocalDate.now()
)

@Entity(tableName = "compliance_checks")
data class ComplianceCheck(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val article: String,
    val description: String,
    val status: String = "pending",
    val notes: String = "",
    val dateChecked: LocalDate? = null
)

@Entity(tableName = "risk_assessments")
data class RiskAssessment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val activityId: Long,
    val likelihood: Int = 1,
    val impact: Int = 1,
    val riskLevel: String = "low",
    val mitigation: String = "",
    val dateAssessed: LocalDate = LocalDate.now()
)

@Entity(tableName = "breach_logs")
data class BreachLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val affectedUsers: Int = 0,
    val remediation: String = "",
    val severity: String = "medium",
    val dateOccurred: LocalDate = LocalDate.now(),
    val dateResolved: LocalDate? = null
)