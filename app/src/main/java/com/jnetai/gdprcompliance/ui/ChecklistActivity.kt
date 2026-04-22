package com.jnetai.gdprcompliance.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jnetai.gdprcompliance.GDPRApp
import com.jnetai.gdprcompliance.data.ComplianceCheck
import com.jnetai.gdprcompliance.databinding.ActivityChecklistBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class ChecklistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChecklistBinding
    private val app get() = application as GDPRApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChecklistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "GDPR Checklist"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adapter = ChecklistAdapter { check -> toggleCheck(check) }
        binding.checklistRecycler.layoutManager = LinearLayoutManager(this)
        binding.checklistRecycler.adapter = adapter

        lifecycleScope.launch {
            app.database.dao().getAllChecks().collectLatest { checks ->
                adapter.submitList(checks)
                binding.emptyView.visibility = if (checks.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            }
        }

        binding.addCheckButton.setOnClickListener {
            val articles = listOf(
                "Art. 5 - Principles relating to processing", "Art. 6 - Lawfulness of processing",
                "Art. 13 - Information to be provided", "Art. 15 - Right of access",
                "Art. 16 - Right to rectification", "Art. 17 - Right to erasure",
                "Art. 18 - Right to restriction", "Art. 20 - Right to data portability",
                "Art. 21 - Right to object", "Art. 25 - Data protection by design",
                "Art. 30 - Records of processing", "Art. 33 - Notification of breach",
                "Art. 35 - Data protection impact assessment"
            )
            lifecycleScope.launch {
                articles.forEach { a ->
                    app.database.dao().insertCheck(ComplianceCheck(article = a, description = a))
                }
                Toast.makeText(this@ChecklistActivity, "Checklist items added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleCheck(check: ComplianceCheck) {
        val newStatus = if (check.status == "completed") "pending" else "completed"
        lifecycleScope.launch {
            app.database.dao().updateCheck(check.copy(status = newStatus, dateChecked = LocalDate.now()))
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}