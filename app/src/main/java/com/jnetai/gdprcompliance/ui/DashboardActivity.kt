package com.jnetai.gdprcompliance.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.jnetai.gdprcompliance.BuildConfig
import com.jnetai.gdprcompliance.GDPRApp
import com.jnetai.gdprcompliance.R
import com.jnetai.gdprcompliance.data.ComplianceCheck
import com.jnetai.gdprcompliance.databinding.ActivityDashboardBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private val app get() = application as GDPRApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Compliance Dashboard"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lifecycleScope.launch {
            app.database.dao().getAllActivities().collectLatest { activities ->
                binding.activitiesCount.text = "${activities.size} activities"
            }
        }

        lifecycleScope.launch {
            app.database.dao().getAllChecks().collectLatest { checks ->
                val completed = checks.count { it.status == "completed" }
                val total = checks.size
                val pct = if (total > 0) (completed * 100 / total) else 0
                binding.complianceScore.text = "$pct%"
                binding.complianceProgress.progress = pct
                binding.checksCount.text = "$completed/$total checks completed"
            }
        }

        lifecycleScope.launch {
            app.database.dao().getAllBreaches().collectLatest { breaches ->
                binding.breachesCount.text = "${breaches.size} breaches logged"
                val unresolved = breaches.count { it.dateResolved == null }
                binding.unresolvedText.text = "$unresolved unresolved"
            }
        }

        binding.exportButton.setOnClickListener {
            lifecycleScope.launch { exportData() }
        }
    }

    private suspend fun exportData() {
        val gson = Gson()
        val data = mapOf(
            "compliance_checks" to emptyList<ComplianceCheck>(),
            "processing_activities" to emptyList<com.jnetai.gdprcompliance.data.ProcessingActivity>(),
            "risk_assessments" to emptyList<com.jnetai.gdprcompliance.data.RiskAssessment>(),
            "breach_logs" to emptyList<com.jnetai.gdprcompliance.data.BreachLog>()
        )
        val json = gson.toJson(data)
        try {
            val file = java.io.File(getExternalFilesDir(null), "gdpr_compliance_export.json")
            file.writeText(json)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, androidx.core.content.FileProvider.getUriForFile(
                    this@DashboardActivity, "${packageName}.fileprovider", file))
                putExtra(Intent.EXTRA_SUBJECT, "GDPR Compliance Report")
            }
            startActivity(Intent.createChooser(shareIntent, "Export GDPR Report"))
        } catch (e: Exception) {
            Toast.makeText(this, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}