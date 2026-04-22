package com.jnetai.gdprcompliance.ui

import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.jnetai.gdprcompliance.BuildConfig
import com.jnetai.gdprcompliance.GDPRApp
import com.jnetai.gdprcompliance.R
import com.jnetai.gdprcompliance.databinding.ActivityDashboardBinding
import kotlinx.coroutines.launch
import java.net.URL
import org.json.JSONObject

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
            val activities = app.database.dao().getAllActivities()
            val checks = app.database.dao().getAllChecks()
            val risks = app.database.dao().getAllRisks()
            val breaches = app.database.dao().getAllBreaches()

            activities.collectLatest { acts ->
                binding.activitiesCount.text = "${acts.size} activities"
            }
            // Collect from flows separately
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
            lifecycleScope.launch {
                exportData()
            }
        }
    }

    private suspend fun exportData() {
        // Export all data as JSON
        val acts = mutableListOf<com.jnetai.gdprcompliance.data.ProcessingActivity>()
        val checks = mutableListOf<com.jnetai.gdprcompliance.data.ComplianceCheck>()
        val risks = mutableListOf<com.jnetai.gdprcompliance.data.RiskAssessment>()
        val breaches = mutableListOf<com.jnetai.gdprcompliance.data.BreachLog>()

        val gson = Gson()
        val data = mapOf(
            "processing_activities" to acts,
            "compliance_checks" to checks,
            "risk_assessments" to risks,
            "breach_logs" to breaches
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