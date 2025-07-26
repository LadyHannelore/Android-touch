package com.touchsimulator.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PositionSelectorActivity : AppCompatActivity() {

    private lateinit var instructionText: TextView
    private lateinit var coordinatesText: TextView
    private lateinit var confirmButton: Button
    private lateinit var cancelButton: Button
    private lateinit var overlayView: View

    private var selectedX: Float = 0f
    private var selectedY: Float = 0f
    private var hasSelection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_position_selector)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        instructionText = findViewById(R.id.instructionText)
        coordinatesText = findViewById(R.id.coordinatesText)
        confirmButton = findViewById(R.id.confirmButton)
        cancelButton = findViewById(R.id.cancelButton)
        overlayView = findViewById(R.id.overlayView)

        confirmButton.isEnabled = false
    }

    private fun setupClickListeners() {
        overlayView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                selectedX = event.rawX
                selectedY = event.rawY
                hasSelection = true

                coordinatesText.text = "Selected Position: (${selectedX.toInt()}, ${selectedY.toInt()})"
                confirmButton.isEnabled = true

                Toast.makeText(this, "Position selected! Tap Confirm to save.", Toast.LENGTH_SHORT).show()
            }
            true
        }

        confirmButton.setOnClickListener {
            if (hasSelection) {
                val resultIntent = Intent()
                resultIntent.putExtra("x", selectedX)
                resultIntent.putExtra("y", selectedY)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
