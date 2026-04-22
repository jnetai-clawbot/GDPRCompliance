package com.jnetai.gdprcompliance.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jnetai.gdprcompliance.GDPRApp
import com.jnetai.gdprcompliance.data.RiskAssessment
import com.jnetai.gdprcompliance.databinding.ActivityDetailBinding
import kotlinx.coroutines.launch
import java.time.LocalDate

class ActivityDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val app get() = application as GDPRApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val activityId = intent.getLongExtra("id", -1)
        if (activityId == -1L) { finish(); return }

        lifecycleScope.launch {
            val activity = app.database.dao().getActivity(activityId)
            binding.nameText.text = activity.name
            binding.descText.text = activity.description
            binding.basisText.text = "Legal basis: ${activity.legalBasis}"
            binding.categoryText.text = activity.dataCategories
            binding.dateText.text = "Added: ${activity.dateAdded}"

            val risks = app.database.dao().getRisksForActivity(activityId)
            if (risks.isNotEmpty()) {
                val r = risks.first()
                binding.riskLikelihood.text = "Likelihood: ${r.likelihood}/5"
                binding.riskImpact.text = "Impact: ${r.impact}/5"
                binding.riskLevel.text = "Risk: ${r.riskLevel.uppercase()}"
                binding.mitigationText.text = r.mitigation
            }
        }

        binding.saveRiskButton.setOnClickListener {
            val likelihood = binding.likelihoodSlider.value.toInt()
            val impact = binding.impactSlider.value.toInt()
            val riskLevel = when {
                likelihood * impact >= 20 -> "critical"
                likelihood * impact >= 12 -> "high"
                likelihood * impact >= 6 -> "medium"
                else -> "low"
            }
            val mitigation = binding.mitigationInput.text.toString().trim()

            lifecycleScope.launch {
                app.database.dao().insertRisk(RiskAssessment(
                    activityId = activityId, likelihood = likelihood, impact = impact,
                    riskLevel = riskLevel, mitigation = mitigation, dateAssessed = LocalDate.now()
                ))
                Toast.makeText(this@ActivityDetailActivity, "Risk assessment saved", Toast.LENGTH_SHORT).show()
                recreate()
            }
        }

        binding.deleteButton.setOnClickListener {
            lifecycleScope.launch {
                val activity = app.database.dao().getActivity(activityId)
                app.database.dao().deleteActivity(activity)
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}