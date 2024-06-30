package com.gruporihappy.fortivi.auth.logic

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
import com.gruporihappy.fortivi.R

class ConnectionManagerService: Service() {
    private lateinit var connectivityCallback: ConnectivityCallback
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action){
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> {
                AuthFlowLogs.setIsRunning(false)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun start() {
        val notification = NotificationCompat.Builder(applicationContext, "manager_channel").setAutoCancel(false).setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle("Running FortiGate").setContentText("Logs go here").setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE).setOngoing(true).setPriority(NotificationCompat.PRIORITY_HIGH).build()
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityCallback = ConnectivityCallback(this)

        // Register the NetworkCallback to listen for network changes
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, connectivityCallback)
        AuthFlowLogs.setIsRunning(true)
        startForeground(1, notification)
    }

    enum class Actions {
        START, STOP
    }


}