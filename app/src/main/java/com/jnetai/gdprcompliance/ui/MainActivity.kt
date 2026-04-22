package com.jnetai.gdprcompliance.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jnetai.gdprcompliance.GDPRApp
import com.jnetai.gdprcompliance.R
import com.jnetai.gdprcompliance.data.ProcessingActivity
import com.jnetai.gdprcompliance.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ActivityAdapter
    private val app get() = application as GDPRApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "GDPR Compliance"

        adapter = ActivityAdapter { activity -> openDetail(activity) }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            app.database.dao().getAllActivities().collectLatest { activities ->
                adapter.submitList(activities)
                binding.emptyView.visibility = if (activities.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            }
        }

        binding.fab.setOnClickListener { startActivity(Intent(this, AddActivityActivity::class.java)) }
    }

    private fun openDetail(activity: ProcessingActivity) {
        startActivity(Intent(this, ActivityDetailActivity::class.java).putExtra("id", activity.id))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_dashboard -> { startActivity(Intent(this, DashboardActivity::class.java)); true }
        R.id.action_checklist -> { startActivity(Intent(this, ChecklistActivity::class.java)); true }
        R.id.action_breaches -> { startActivity(Intent(this, BreachLogActivity::class.java)); true }
        R.id.action_about -> { startActivity(Intent(this, AboutActivity::class.java)); true }
        else -> super.onOptionsItemSelected(item)
    }
}