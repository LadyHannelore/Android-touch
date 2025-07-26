package com.touchsimulator.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    private lateinit var statusText: TextView
    private lateinit var setPositionButton: Button
    private lateinit var enableAccessibilityButton: Button
    private var touchX: Float = 0f
    private var touchY: Float = 0f
    private var isPositionSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupClickListeners()
        updateStatus()
    }

    private fun initViews() {
        statusText = findViewById(R.id.statusText)
        setPositionButton = findViewById(R.id.setPositionButton)
        enableAccessibilityButton = findViewById(R.id.enableAccessibilityButton)
    }

    private fun setupClickListeners() {
        setPositionButton.setOnClickListener {
            showPositionSelector()
        }

        enableAccessibilityButton.setOnClickListener {
            openAccessibilitySettings()
        }
    }

    private fun showPositionSelector() {
        val intent = Intent(this, PositionSelectorActivity::class.java)
        startActivityForResult(intent, REQUEST_POSITION)
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
        Toast.makeText(this, "Enable Touch Simulator accessibility service", Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_POSITION && resultCode == Activity.RESULT_OK) {
            data?.let {
                touchX = it.getFloatExtra("x", 0f)
                touchY = it.getFloatExtra("y", 0f)
                isPositionSet = true
                updateStatus()
                
                // Save position to shared preferences
                val prefs = getSharedPreferences("touch_position", MODE_PRIVATE)
                prefs.edit()
                    .putFloat("x", touchX)
                    .putFloat("y", touchY)
                    .putBoolean("position_set", true)
                    .apply()
            }
        }
    }

    private fun updateStatus() {
        val accessibilityEnabled = isAccessibilityServiceEnabled()
        val overlayPermission = Settings.canDrawOverlays(this)
        
        val status = buildString {
            append("Status:\n")
            append("• Accessibility Service: ${if (accessibilityEnabled) "✓ Enabled" else "✗ Disabled"}\n")
            append("• Overlay Permission: ${if (overlayPermission) "✓ Granted" else "✗ Not granted"}\n")
            append("• Touch Position: ${if (isPositionSet) "✓ Set ($touchX, $touchY)" else "✗ Not set"}\n")
            append("\nPress Volume Down to simulate touch!")
        }
        
        statusText.text = status
        
        if (!overlayPermission) {
            requestOverlayPermission()
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityEnabled = Settings.Secure.getInt(
            contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED, 0
        )
        
        if (accessibilityEnabled == 1) {
            val services = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            return services?.contains("${packageName}/.VolumeKeyService") == true
        }
        return false
    }

    private fun requestOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
        loadSavedPosition()
    }

    private fun loadSavedPosition() {
        val prefs = getSharedPreferences("touch_position", MODE_PRIVATE)
        if (prefs.getBoolean("position_set", false)) {
            touchX = prefs.getFloat("x", 0f)
            touchY = prefs.getFloat("y", 0f)
            isPositionSet = true
        }
    }

    companion object {
        private const val REQUEST_POSITION = 1001
    }
}
