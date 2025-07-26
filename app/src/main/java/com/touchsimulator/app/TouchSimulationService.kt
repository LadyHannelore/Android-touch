package com.touchsimulator.app

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class TouchSimulationService : AccessibilityService() {
    
    private var touchX = 500f
    private var touchY = 500f
    
    companion object {
        private const val TAG = "TouchSimulationService"
        var instance: TouchSimulationService? = null
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d(TAG, "TouchSimulationService created")
    }
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "TouchSimulationService connected")
        Toast.makeText(this, "Touch Simulation Service enabled", Toast.LENGTH_SHORT).show()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                "SET_COORDINATES" -> {
                    touchX = it.getFloatExtra("x", touchX)
                    touchY = it.getFloatExtra("y", touchY)
                    Log.d(TAG, "Coordinates updated: ($touchX, $touchY)")
                }
                "SIMULATE_TOUCH" -> {
                    val x = it.getFloatExtra("x", touchX)
                    val y = it.getFloatExtra("y", touchY)
                    simulateTouch(x, y)
                }
            }
        }
        return START_STICKY
    }
    
    fun simulateTouch(x: Float = touchX, y: Float = touchY) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val path = Path().apply {
                moveTo(x, y)
            }
            
            val gestureBuilder = GestureDescription.Builder()
            val strokeDescription = GestureDescription.StrokeDescription(path, 0, 100)
            gestureBuilder.addStroke(strokeDescription)
            
            val gesture = gestureBuilder.build()
            
            val result = dispatchGesture(gesture, object : GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    Log.d(TAG, "Touch gesture completed at ($x, $y)")
                }
                
                override fun onCancelled(gestureDescription: GestureDescription?) {
                    Log.e(TAG, "Touch gesture cancelled")
                }
            }, null)
            
            if (!result) {
                Log.e(TAG, "Failed to dispatch touch gesture")
            }
        } else {
            Log.e(TAG, "Touch simulation requires Android N (API 24) or higher")
        }
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // We don't need to handle accessibility events for touch simulation
    }
    
    override fun onInterrupt() {
        Log.d(TAG, "TouchSimulationService interrupted")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        instance = null
        Log.d(TAG, "TouchSimulationService destroyed")
    }
}
