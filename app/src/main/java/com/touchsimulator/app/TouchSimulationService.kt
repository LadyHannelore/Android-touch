package com.touchsimulator.app

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class TouchSimulationService : Service() {

    companion object {
        private const val TAG = "TouchSimulationService"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Touch Simulation Service Created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Touch Simulation Service Started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Touch Simulation Service Destroyed")
    }
}
