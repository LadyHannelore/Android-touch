package com.touchsimulator.app

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class VolumeKeyService : AccessibilityService() {

    companion object {
        private const val TAG = "VolumeKeyService"
        var instance: VolumeKeyService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d(TAG, "Volume Key Service Connected")
        
        // Start the touch simulation service
        val intent = Intent(this, TouchSimulationService::class.java)
        startService(intent)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // We don't need to handle accessibility events for this use case
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted")
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        event?.let {
            if (it.action == KeyEvent.ACTION_DOWN) {
                when (it.keyCode) {
                    KeyEvent.KEYCODE_VOLUME_DOWN -> {
                        Log.d(TAG, "Volume Down pressed")
                        performTouch()
                        return true // Consume the event to prevent volume change
                    }
                    KeyEvent.KEYCODE_VOLUME_UP -> {
                        // Optional: You can add functionality for volume up if needed
                        Log.d(TAG, "Volume Up pressed")
                        return false // Let the system handle volume up normally
                    }
                }
            }
        }
        return super.onKeyEvent(event)
    }

    private fun performTouch() {
        val prefs = getSharedPreferences("touch_position", MODE_PRIVATE)
        val isPositionSet = prefs.getBoolean("position_set", false)
        
        if (!isPositionSet) {
            Toast.makeText(this, "Touch position not set. Please set it in the app.", Toast.LENGTH_SHORT).show()
            return
        }

        val x = prefs.getFloat("x", 0f)
        val y = prefs.getFloat("y", 0f)

        Log.d(TAG, "Performing touch at ($x, $y)")

        // Create a gesture description for the touch
        val path = Path()
        path.moveTo(x, y)

        val gestureBuilder = GestureDescription.Builder()
        val strokeDescription = GestureDescription.StrokeDescription(path, 0, 100)
        gestureBuilder.addStroke(strokeDescription)

        val gesture = gestureBuilder.build()

        // Perform the gesture
        val result = dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                Log.d(TAG, "Touch gesture completed successfully")
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                Log.e(TAG, "Touch gesture was cancelled")
            }
        }, null)

        if (!result) {
            Log.e(TAG, "Failed to dispatch touch gesture")
            Toast.makeText(this, "Failed to perform touch", Toast.LENGTH_SHORT).show()
        } else {
            // Visual feedback
            Toast.makeText(this, "Touch simulated!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        Log.d(TAG, "Volume Key Service Destroyed")
    }
}
