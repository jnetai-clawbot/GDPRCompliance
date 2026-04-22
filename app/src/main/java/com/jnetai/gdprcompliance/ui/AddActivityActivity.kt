package com.jnetai.gdprcompliance.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jnetai.gdprcompliance.GDPRApp
import com.jnetai.gdprcompliance.data.ProcessingActivity
import com.jnetai.gdprcompliance.databinding.ActivityAddActivityBinding
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddActivityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddActivityBinding
    private val app get() = application as GDPRApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Add Processing Activity"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val legalBases = listOf("Consent", "Contract", "Legal Obligation", "Vital Interests", "Public Task", "Legitimate Interest")
        binding.legalBasisSpinner.adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, legalBases)

        binding.saveButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val desc = binding.descEditText.text.toString().trim()
            val categories = binding.categoriesEditText.text.toString().trim()
            val basis = binding.legalBasisSpinner.selectedItem.toString()

            if (name.isBlank()) {
                Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                app.database.dao().insertActivity(ProcessingActivity(
                    name = name, description = desc, legalBasis = basis,
                    dataCategories = categories, dateAdded = LocalDate.now()
                ))
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}