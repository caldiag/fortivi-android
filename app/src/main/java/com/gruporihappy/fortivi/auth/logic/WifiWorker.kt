import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.gruporihappy.fortivi.auth.logic.AuthFlow
import com.gruporihappy.fortivi.viewmodel.logs.CredentialsViewModel

class WifiWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val application = applicationContext as Application
        val credsViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(CredentialsViewModel::class.java)
        val handler = Handler(Looper.getMainLooper())

        handler.post {
            Toast.makeText(applicationContext, "FortiGate: authenticating", Toast.LENGTH_SHORT).show()
        }
        val auth = AuthFlow(credsViewModel.username, credsViewModel.password, applicationContext)
        auth.start()
        return Result.success()
    }

}
