package com.gruporihappy.fortivi.auth.logic

import AuthFlowLogs
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
    private val url = "http://www.fast.net/"
    private var validateUrl = "http://192.168.102.1:1003/fgtauth?"
    private val postUrl = "http://192.168.102.1:1000/"
    private val prefs = context.getSharedPreferences("Credentials", Context.MODE_PRIVATE)
    private var username = if (prefs.contains("username")) prefs.getString("username", "") ?: "" else CredentialsLogs.readUsername()
    private var password = if (prefs.contains("password")) prefs.getString("password", "") ?: "" else CredentialsLogs.readPassword()
    private val thisContext = context
    private val pushLog = PushLogs(thisContext)

    fun start() {
        pushLog.new("Start: fetching credentials")
        if(username == "" || password == ""){
            stop("Could not auto-start: no credentials saved", retry = false)
            Intent(thisContext, ConnectionManagerService::class.java).also {
                it.action = ConnectionManagerService.Actions.STOP.toString()
                thisContext.startService(it)
            }
            return
        }
        pushLog.new("Attempting HTTP Request to Web with user $username")
        getMagic()
    }

    fun stop(reason: String, retry: Boolean = true, finished: Boolean = false) {
        pushLog.new("INTERRUPTED: $reason" + if(retry) ". Trying again in 10 seconds" else "")
        //this stops the service if an error (such as end of stream errors)
        // occurred from the auth flow and retry was not requested
        if (!retry && !finished){
            Intent(thisContext, ConnectionManagerService::class.java).also {
                it.action = ConnectionManagerService.Actions.STOP.toString()
                thisContext.startService(it)
            }
            return
        }

        //if retry was not requested and finished is set to true, don't do anything
        //this way the app will sit doing nothing but listening for connection changes
        //so it can restart auth flow if wifi state changes
        if (finished) return

        android.os.Handler(Looper.getMainLooper()).postDelayed(
            {
                if (AuthFlowLogs.readIsRunning().value) start()
            },
            10000
        )
    }

    private fun getMagic() {
        if (!AuthFlowLogs.readIsRunning().value) return
        queue.add(StringRequest(
            Request.Method.GET, url,
            { response ->
                //stupid and unfathomable
                magic = response.substring(response.indexOf("?")+1, response.indexOf("?")+17)

                pushLog.new("Got possible magic ID: $magic")
                validateMagic()
            },
            { error ->
                checkConnection()
                stop("Could not get magic ID. $error")
            }
        ))
    }

    private fun validateMagic() {
        if (!AuthFlowLogs.readIsRunning().value) return
        validateUrl = "$validateUrl$magic"
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
        if (!AuthFlowLogs.readIsRunning().value) return
        pushLog.new("Pushing authentication payload")

        val pureText = "4tredir=$url&magic=$magic&username=$username&password=$password"
        queue.add(
            object : StringRequest(Method.POST, postUrl,
                Response.Listener {
                    stop("Authenticated", finished = true)
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
        queue.add(StringRequest(Request.Method.GET, "https://caldiag.github.io/helloworld/", { response ->
            if (response == "helloworld") stop("Authenticated", finished = true)
        }, {
            stop("Could not validate connection. Trying to authenticate again.")
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
