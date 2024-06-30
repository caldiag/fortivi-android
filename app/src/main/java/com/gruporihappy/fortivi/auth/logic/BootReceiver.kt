package com.gruporihappy.fortivi.auth.logic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        PushLogs(context).new("Waiting for connection...")
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Intent(context, ConnectionManagerService::class.java).also {
                it.action = ConnectionManagerService.Actions.START.toString()
                context.startService(it)
            }
        }
    }
}