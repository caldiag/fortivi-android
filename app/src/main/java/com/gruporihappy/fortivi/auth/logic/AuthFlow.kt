package com.gruporihappy.fortivi.auth.logic

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class AuthFlow (private val username: String, private val password: String, context: Context, private val logs: SnapshotStateList<String>){
    private val queue = Volley.newRequestQueue(context)
    private var magic = ""
    private val url = "https://speedtest.net/"
    private val postUrl = "http://10.105.8.1:1000"
    private val validateUrl = "http://10.105.8.1:1003/fgtauth?${magic}"

    fun start(){
        logs.add("Attempt HTTP Request to Web")
        getMagic()
    }

    private fun getMagic(){
        queue.add(StringRequest(
//      get speedtest, if unauthenticated will return html code to navigate to FG captive portal
            Request.Method.GET, url,
            { response ->
                magic = response.substring(91, 107)
                logs.add("Got possible magic ID: $magic")
                validateMagic()
            },
            { error ->
                logs.add("Could not get Magic ID. $error")
                logs.add("Stopped.")
            }
        ))
    }

    private fun validateMagic(){
        queue.add(StringRequest(
            Request.Method.GET, validateUrl,
            {
                logs.add("Magic ID validated")
                authenticate()
            },
            { error ->
                logs.add("Could not validate Magic ID. $error")
                logs.add("Stopped.")
            }
        ))
    }
    private fun authenticate(){
        val params = HashMap<String, String>()
        params["4Tredir"] = url
        params["magic"] = magic
        params["username"] = username
        params["password"] = password

        queue.add(
            object : StringRequest(Method.POST, postUrl,
                Response.Listener {
                    logs.add("Magic ID validated")
                },
                Response.ErrorListener { error ->
                    logs.add("Could not validate Magic ID. $error")
                    logs.add("Stopped.")
                }) {

                override fun getParams(): MutableMap<String, String> {
                    return params
                }
            }
        )
    }

}
