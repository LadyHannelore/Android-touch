package com.touchsimulator.app

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import androidx.core.app.NotificationCompat

class VolumeKeyService : Service() {
    
    private lateinit var audioManager: AudioManager
    private lateinit var notificationManager: NotificationManager
    private val handler = Handler(Looper.getMainLooper())
    
    companion object {
        private const val TAG = "VolumeKeyService"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "TOUCH_SIMULATOR_CHANNEL"
    }
    
    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d(TAG, "VolumeKeyService created")
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        startVolumeMonitoring()
        Log.d(TAG, "VolumeKeyService started")
        return START_STICKY
    }
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Touch Simulator")
            .setContentText("Press Volume Down to simulate touch")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .build()
    }
    
    private fun startVolumeMonitoring() {
        // Note: This is a simplified approach. In a real app, you might need to use
        // a system overlay or accessibility service to capture volume keys globally
        val volumeMonitorRunnable = object : Runnable {
            private var lastVolumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            
            override fun run() {
                val currentVolumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                
                if (currentVolumeLevel < lastVolumeLevel) {
                    // Volume down detected
                    onVolumeDownPressed()
                    // Restore volume to prevent actual volume change
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC, 
                        lastVolumeLevel, 
                        0
                    )
                } else {
                    lastVolumeLevel = currentVolumeLevel
                }
                
                // Check again after a short delay
                handler.postDelayed(this, 100)
            }
        }
        
        handler.post(volumeMonitorRunnable)
    }
    
    private fun onVolumeDownPressed() {
        Log.d(TAG, "Volume down pressed - simulating touch")
        
        // Trigger touch simulation
        TouchSimulationService.instance?.simulateTouch()
        
        // Update notification to show last touch
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Touch Simulator")
            .setContentText("Touch simulated! (${System.currentTimeMillis()})")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID, notification)
        
        // Reset notification after 2 seconds
        handler.postDelayed({
            notificationManager.notify(NOTIFICATION_ID, createNotification())
        }, 2000)
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        Log.d(TAG, "VolumeKeyService destroyed")
    }
}
