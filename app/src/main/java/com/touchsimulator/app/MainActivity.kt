package com.touchsimulator.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

class MainActivity : AppCompatActivity() {
    
    private lateinit var xCoordinateEdit: EditText
    private lateinit var yCoordinateEdit: EditText
    private lateinit var statusText: TextView
    private lateinit var setCoordinatesBtn: Button
    private lateinit var enableServiceBtn: Button
    private lateinit var testTouchBtn: Button
    
    private var touchX = 500f
    private var touchY = 500f
    
    companion object {
        const val OVERLAY_PERMISSION_REQUEST_CODE = 1234
        const val ACCESSIBILITY_PERMISSION_REQUEST_CODE = 5678
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupClickListeners()
        updateStatus()
        createNotificationChannel()
    }
    
    private fun initViews() {
        xCoordinateEdit = findViewById(R.id.xCoordinateEdit)
        yCoordinateEdit = findViewById(R.id.yCoordinateEdit)
        statusText = findViewById(R.id.statusText)
        setCoordinatesBtn = findViewById(R.id.setCoordinatesBtn)
        enableServiceBtn = findViewById(R.id.enableServiceBtn)
        testTouchBtn = findViewById(R.id.testTouchBtn)
        
        // Set default coordinates
        xCoordinateEdit.setText(touchX.toInt().toString())
        yCoordinateEdit.setText(touchY.toInt().toString())
    }
    
    private fun setupClickListeners() {
        setCoordinatesBtn.setOnClickListener {
            setTouchCoordinates()
        }
        
        enableServiceBtn.setOnClickListener {
            requestPermissions()
        }
        
        testTouchBtn.setOnClickListener {
            testTouch()
        }
    }
    
    private fun setTouchCoordinates() {
        try {
            val x = xCoordinateEdit.text.toString().toFloat()
            val y = yCoordinateEdit.text.toString().toFloat()
            
            if (x >= 0 && y >= 0) {
                touchX = x
                touchY = y
                
                // Send coordinates to the service
                val intent = Intent(this, TouchSimulationService::class.java).apply {
                    action = "SET_COORDINATES"
                    putExtra("x", touchX)
                    putExtra("y", touchY)
                }
                startService(intent)
                
                Toast.makeText(this, "Touch coordinates set to ($touchX, $touchY)", Toast.LENGTH_SHORT).show()
                updateStatus()
            } else {
                Toast.makeText(this, "Please enter valid coordinates", Toast.LENGTH_SHORT).show()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun requestPermissions() {
        // Check overlay permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
                return
            }
        }
        
        // Check accessibility permission
        if (!isAccessibilityServiceEnabled()) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivityForResult(intent, ACCESSIBILITY_PERMISSION_REQUEST_CODE)
            Toast.makeText(this, "Please enable Touch Simulator accessibility service", Toast.LENGTH_LONG).show()
            return
        }
        
        // Start the volume key service
        startVolumeKeyService()
    }
    
    private fun isAccessibilityServiceEnabled(): Boolean {
        val service = ComponentName(this, TouchSimulationService::class.java)
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains(service.flattenToString()) == true
    }
    
    private fun startVolumeKeyService() {
        val intent = Intent(this, VolumeKeyService::class.java)
        startForegroundService(intent)
        updateStatus()
        Toast.makeText(this, "Service started! Press Volume Down to simulate touch", Toast.LENGTH_LONG).show()
    }
    
    private fun testTouch() {
        val intent = Intent(this, TouchSimulationService::class.java).apply {
            action = "SIMULATE_TOUCH"
            putExtra("x", touchX)
            putExtra("y", touchY)
        }
        startService(intent)
    }
    
    private fun updateStatus() {
        val overlayPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
        
        val accessibilityPermission = isAccessibilityServiceEnabled()
        
        val status = buildString {
            appendLine("Touch coordinates: ($touchX, $touchY)")
            appendLine("Overlay permission: ${if (overlayPermission) "✓" else "✗"}")
            appendLine("Accessibility service: ${if (accessibilityPermission) "✓" else "✗"}")
            if (overlayPermission && accessibilityPermission) {
                appendLine("\n✓ Ready! Press Volume Down to simulate touch")
            } else {
                appendLine("\n⚠ Enable permissions to use the app")
            }
        }
        
        statusText.text = status
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Touch Simulator Service"
            val descriptionText = "Background service for touch simulation"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel("TOUCH_SIMULATOR_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            OVERLAY_PERMISSION_REQUEST_CODE,
            ACCESSIBILITY_PERMISSION_REQUEST_CODE -> {
                updateStatus()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        updateStatus()
    }
}
