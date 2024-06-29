package com.gruporihappy.fortivi.auth.logic

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.gruporihappy.fortivi.viewmodel.logs.CredentialsViewModel
import com.gruporihappy.fortivi.viewmodel.logs.LogsViewModel
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class AuthFlow (private val username: MutableState<String>, private val password: MutableState<String>, context: Context) {
    private val queue = Volley.newRequestQueue(context, HurlStack(null, createSslSocketFactory()))
    private var magic = ""
    private val url = "http://www.bing.com/"
    private var validateUrl = "http://192.168.102.1:1003/fgtauth?"
    private val postUrl = "http://192.168.102.1:1000/"
    private var isConnecting = false
    private val logsViewModel: LogsViewModel by lazy {
        ViewModelProvider(context as Application as ViewModelStoreOwner).get(LogsViewModel::class.java)
    }

    fun start() {
        logsViewModel.add("Attempt HTTP Request to Web")
        println("username is ${username} and password is $password")
        println(logsViewModel.read().value.last())
        isConnecting = true
        getMagic()
    }

    fun stop() {
        queue.stop()
        isConnecting = false
        logsViewModel.add("Stopped.")
    }

    private fun getMagic() {
        queue.add(StringRequest(
            Request.Method.GET, url,
            { response ->
                //stupid and unfathomable
                magic = response.substring(response.indexOf("?")+1, response.indexOf("?")+17)

                logsViewModel.add("Got possible magic ID: $magic from $response")
                validateMagic()
            },
            { error ->
                logsViewModel.add("Could not get magic ID. $error")
                isConnecting = false
                logsViewModel.add("Stopped at getting magic.")
            }
        ))
    }

    private fun validateMagic() {
        validateUrl = "https://192.168.102.1:1003/fgtauth?$magic"
        queue.add(StringRequest(
            Request.Method.GET, validateUrl,
            {
                logsViewModel.add("Magic ID validated at $validateUrl")
                authenticate()
            },
            { error ->
                logsViewModel.add("Could not validate Magic ID at$validateUrl. $error.")
                this.stop()
            }
        ))
    }

    private fun authenticate() {
        logsViewModel.add("Pushing authentication payload")

        val pureText = "4tredir=$url&magic=$magic&username=$username&password=$password"
        queue.add(
            object : StringRequest(Method.POST, postUrl,
                Response.Listener {
                    logsViewModel.add("Authenticated")
                    this.stop()
                },
                Response.ErrorListener {
                    logsViewModel.add("Got end of stream (maybe expected). Test your connection!")
                    this.stop()
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
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
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
