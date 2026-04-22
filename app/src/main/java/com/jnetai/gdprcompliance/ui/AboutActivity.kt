package com.jnetai.gdprcompliance.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jnetai.gdprcompliance.BuildConfig
import com.jnetai.gdprcompliance.databinding.ActivityAboutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "About"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.versionText.text = "Version ${BuildConfig.VERSION_NAME}"
        binding.appNameText.text = "GDPR Compliance Checker"

        binding.updateButton.setOnClickListener {
            checkForUpdates()
        }

        binding.shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Check out GDPR Compliance Checker! https://github.com/jnetai-clawbot/GDPRCompliance")
            }
            startActivity(Intent.createChooser(shareIntent, "Share"))
        }
    }

    private fun checkForUpdates() {
        binding.updateStatus.text = "Checking..."
        Thread {
            try {
                val json = URL("https://api.github.com/repos/jnetai-clawbot/GDPRCompliance/releases/latest").readText()
                val latest = JSONObject(json).optString("tag_name", "v0.0.0")
                val currentVersion = "v${BuildConfig.VERSION_NAME}"
                runOnUiThread {
                    if (latest != currentVersion) {
                        binding.updateStatus.text = "Update available: $latest"
                        val url = JSONObject(json).optString("html_url", "")
                        if (url.isNotEmpty()) startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } else {
                        binding.updateStatus.text = "You're up to date!"
                    }
                }
            } catch (e: Exception) {
                runOnUiThread { binding.updateStatus.text = "Check failed: ${e.message}" }
            }
        }.start()
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}