package com.gruporihappy.fortivi.auth.logic

import com.gruporihappy.fortivi.auth.data.AuthFlowLogs
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters

class WifiWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        if (AuthFlowLogs.readIsAuthenticating().value) return Result.failure()
        val handler = Handler(Looper.getMainLooper())

        handler.post {
            Toast.makeText(applicationContext, "Fortivi: authenticating", Toast.LENGTH_SHORT).show()
        }

        val auth = AuthFlow(applicationContext)
        auth.start()
        return Result.success()
    }
}