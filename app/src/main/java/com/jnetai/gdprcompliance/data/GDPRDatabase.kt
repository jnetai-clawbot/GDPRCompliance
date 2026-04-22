package com.jnetai.gdprcompliance.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ProcessingActivity::class, ComplianceCheck::class, RiskAssessment::class, BreachLog::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class GDPRDatabase : RoomDatabase() {
    abstract fun dao(): GDPRDao

    companion object {
        @Volatile private var INSTANCE: GDPRDatabase? = null
        fun getInstance(context: Context): GDPRDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context, GDPRDatabase::class.java, "gdpr_compliance.db")
                .fallbackToDestructiveMigration().build().also { INSTANCE = it }
        }
    }
}