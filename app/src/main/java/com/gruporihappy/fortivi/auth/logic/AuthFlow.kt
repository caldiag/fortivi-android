package com.gruporihappy.fortivi.auth.logic

import com.gruporihappy.fortivi.auth.data.AuthFlowLogs
import CredentialsLogs
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Looper
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class AuthFlow (context: Context) {
    private val queue = Volley.newRequestQueue(context, HurlStack(null, createSslSocketFactory()))
    private var magic = ""
    private val url = "https://caldiag.github.io/helloworld/lorem"
    private var validateUrl = "http://192.168.102.1:1003/fgtauth?"
    private val postUrl = "http://192.168.102.1:1000/"
    private val prefs = context.getSharedPreferences("Credentials", Context.MODE_PRIVATE)
    private var username = if (prefs.contains("username")) prefs.getString("username", "") ?: "" else CredentialsLogs.readUsername()
    private var password = if (prefs.contains("password")) prefs.getString("password", "") ?: "" else CredentialsLogs.readPassword()
    private val thisContext = context
    private val pushLog = PushLogs(thisContext)

    fun start() {
        AuthFlowLogs.setIsAuthenticating(true)
        pushLog.new("Start: fetching credentials")
        if(username == "" || password == ""){
            stop("Could not auto-start: no credentials saved", retry = false)
            return
        }
        checkConnection()
    }

    fun stop(reason: String, retry: Boolean = true, success: Boolean = false) {
        //if we got here and authenticating is already set to false
        //this means something else already stopped the services. retreat
        if (!AuthFlowLogs.readIsAuthenticating().value) return

        pushLog.new("${if (success) "SUCCESS:" else "INTERRUPTED:"} $reason" + if(retry && !success) ". Trying again in 10 seconds" else "", notId = if (!retry && !success) 2 else 1)

        //this stops the service if an error (such as end of stream errors)
        //occurred from the auth flow and retry was not requested
        if (!retry && !success){
            Intent(thisContext, ConnectionManagerService::class.java).also {
                it.action = ConnectionManagerService.Actions.STOP.toString()
                thisContext.startService(it)
            }
            AuthFlowLogs.setIsServiceRunning(false)
            return
        }

        //if retry was not requested and finished is set to true, don't do anything
        //this way the app will sit doing nothing but listening for connection changes
        //so it can restart auth flow if wifi state changes
        if (success) {
            AuthFlowLogs.setIsAuthenticating(false)
            return
        }

        android.os.Handler(Looper.getMainLooper()).postDelayed(
            {
                if (AuthFlowLogs.readIsServiceRunning().value) start()
            },
            10000
        )
    }

    private fun getMagic() {
        if (!AuthFlowLogs.readIsServiceRunning().value) return
        queue.cache.clear()
        queue.add(StringRequest(
            Request.Method.GET, url,
            { response ->
                //stupid and unfathomable
                magic = response.substring(response.indexOf("?")+1, response.indexOf("?")+17)
                pushLog.new("Got possible magic ID: $magic")

                validateMagic()
            },
            { error ->
                stop("Could not get magic ID. $error")
            }
        ))
    }

    private fun validateMagic() {
        if (!AuthFlowLogs.readIsServiceRunning().value) return
        validateUrl = "$validateUrl$magic"
        queue.cache.clear()
        queue.add(StringRequest(
            Request.Method.GET, validateUrl,
            {
                pushLog.new("Magic ID validated at $validateUrl")
                authenticate()
            },
            { error ->
                stop("Could not validate Magic ID at $validateUrl. $error")
            }
        ))
    }

    private fun authenticate() {
        if (!AuthFlowLogs.readIsServiceRunning().value) return
        pushLog.new("Pushing authentication payload")

        val pureText = "4tredir=$url&magic=$magic&username=$username&password=$password"
        queue.cache.clear()
        queue.add(
            object : StringRequest(Method.POST, postUrl,
                Response.Listener {
                    stop("Authenticated", success = true)
                },
                Response.ErrorListener {
                    pushLog.new("Got end of stream (maybe expected). Testing your connection!")
                    checkConnection()
                }) {
                override fun getBodyContentType(): String {
                    return "text/plain; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return pureText.toByteArray(Charsets.UTF_8)
                }
            }
        )
    }

    private fun checkConnection() {
        pushLog.new("Attempting HTTP request to Web.")
        queue.cache.clear()
        queue.add(StringRequest(Request.Method.GET, "https://caldiag.github.io/helloworld/", { response ->
            if (response == "helloworld") {
                stop("Got $response, authenticated.", success = true)
            }
        }, {
            //retry is false because getMagic retries manually.
            stop("Could not validate connection. Trying to get magic ID", retry = false, success = false)
            getMagic()
        }))
    }


    companion object {
        private fun createSslSocketFactory(): SSLSocketFactory {
            val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Set up a HostnameVerifier that allows all hostnames
            HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }

            return sslContext.socketFactory
        }
    }
}
