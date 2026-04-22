package com.jnetai.gdprcompliance.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jnetai.gdprcompliance.GDPRApp
import com.jnetai.gdprcompliance.data.BreachLog
import com.jnetai.gdprcompliance.databinding.ActivityBreachLogBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class BreachLogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBreachLogBinding
    private val app get() = application as GDPRApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBreachLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Data Breach Log"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adapter = BreachAdapter()
        binding.breachRecycler.layoutManager = LinearLayoutManager(this)
        binding.breachRecycler.adapter = adapter

        val severities = listOf("low", "medium", "high", "critical")
        binding.severitySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, severities)

        lifecycleScope.launch {
            app.database.dao().getAllBreaches().collectLatest { breaches ->
                adapter.submitList(breaches)
                binding.emptyView.visibility = if (breaches.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            }
        }

        binding.addBreachButton.setOnClickListener {
            val title = binding.titleInput.text?.toString()?.trim() ?: ""
            val desc = binding.descInput.text?.toString()?.trim() ?: ""
            val affectedStr = binding.affectedInput.text?.toString()?.trim() ?: "0"
            val affected = affectedStr.toIntOrNull() ?: 0
            val severity = binding.severitySpinner.selectedItem?.toString() ?: "medium"

            if (title.isBlank()) { Toast.makeText(this, "Title required", Toast.LENGTH_SHORT).show(); return@setOnClickListener }

            lifecycleScope.launch {
                app.database.dao().insertBreach(BreachLog(
                    title = title, description = desc, affectedUsers = affected,
                    severity = severity, dateOccurred = LocalDate.now()
                ))
                binding.titleInput.text?.clear()
                binding.descInput.text?.clear()
                binding.affectedInput.text?.clear()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}