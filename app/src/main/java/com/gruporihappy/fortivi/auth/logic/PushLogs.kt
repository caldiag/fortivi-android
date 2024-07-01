package com.gruporihappy.fortivi.auth.logic

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.gruporihappy.fortivi.MainActivity
import com.gruporihappy.fortivi.R
import com.gruporihappy.fortivi.auth.data.AuthFlowLogs

class PushLogs(context: Context) {
    private val thisContext = context

    private val intent = Intent(thisContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
    }
    private val pendingIntent: PendingIntent = PendingIntent.getActivity(thisContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    fun new(log: String, notId: Int = 1) {
        val notificationManager = thisContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val current = AuthFlowLogs.read().plus(log.take(200)).toMutableList()
        AuthFlowLogs.updateWorkResult(current)
        val builder = NotificationCompat.Builder(thisContext, "manager_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Running Fortivi")
            .setContentText(current.last())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        notificationManager.notify(notId, builder.build())
    }
}