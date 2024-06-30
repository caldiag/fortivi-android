package com.gruporihappy.fortivi.auth.logic

import AuthFlowLogs
import CredentialsLogs
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.gruporihappy.fortivi.R
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
    private val url = "http://www.bing.com/"
    private var validateUrl = "http://192.168.102.1:1003/fgtauth?"
    private val postUrl = "http://192.168.102.1:1000/"
    private var isConnecting = false
    private val thisUsername = CredentialsLogs.readUsername()
    private val thisPassword = CredentialsLogs.readPassword()
    private val thisContext = context
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun pushLog(log: String) {
        val current = AuthFlowLogs.read()
        current.add(log.take(200))
        AuthFlowLogs.updateWorkResult(current)
        println(current.joinToString("\n"))
        val builder = NotificationCompat.Builder(thisContext, "manager_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Running FortiGate")
            .setContentText(current.last())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager.notify(1, builder.build())
    }

    fun start() {
        pushLog("Start")
        pushLog("Attempt HTTP Request to Web")
        isConnecting = true
        getMagic()
    }

    fun stop(reason: String) {
        queue.stop()
        isConnecting = false
        pushLog("STOPPED: $reason")
    }

    private fun getMagic() {
        queue.add(StringRequest(
            Request.Method.GET, url,
            { response ->
                //stupid and unfathomable
                magic = response.substring(response.indexOf("?")+1, response.indexOf("?")+17)

                pushLog("Got possible magic ID: $magic")
                validateMagic()
            },
            { error ->
                pushLog("Could not get magic ID. $error")
                isConnecting = false
                pushLog("Stopped at getting magic.")
            }
        ))
    }

    private fun validateMagic() {
        validateUrl = "https://192.168.102.1:1003/fgtauth?$magic"
        queue.add(StringRequest(
            Request.Method.GET, validateUrl,
            {
                pushLog("Magic ID validated at $validateUrl")
                authenticate()
            },
            { error ->
                this.stop("Could not validate Magic ID at $validateUrl. $error.")
            }
        ))
    }

    private fun authenticate() {
        pushLog("Pushing authentication payload")

        val pureText = "4tredir=$url&magic=$magic&username=$thisUsername&password=$thisPassword"
        queue.add(
            object : StringRequest(Method.POST, postUrl,
                Response.Listener {
                    this.stop("Authenticated")
                },
                Response.ErrorListener {
                    this.stop("Got end of stream (maybe expected). Test your connection!")
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
