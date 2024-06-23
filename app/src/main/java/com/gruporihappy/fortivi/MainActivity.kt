package com.gruporihappy.fortivi

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.gruporihappy.fortivi.ui.theme.FortiviTheme
import java.net.URL
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent() {
            enableEdgeToEdge()
            FortiviTheme() {
                Surface(modifier = Modifier
                    .fillMaxSize()
                    ){
                    Home(this)
                }
            }
        }
    }
}

@Composable
fun Home(context: Context) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

        Row (horizontalArrangement = Arrangement.Absolute.SpaceBetween) {
//          Login
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier
                .padding(50.dp, 30.dp)
                .fillMaxWidth(0.5f)) {
                Text(
                    "FortiGate Login",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 20.dp)
                )
                TextField(
                    value = username,
                    onValueChange = { newUsername -> username = newUsername },
                    label = { Text("Username") })
                TextField(
                    value = password,
                    onValueChange = { newPassword -> password = newPassword },
                    label = { Text("Password") })
                Button(onClick = { /*TODO*/ }, Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp)) {
                    Text("Authenticate")
                }
            }
            VerticalDivider(
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxHeight()  //fill the max height
                    .width(2.dp)
            )
//          Logs
            val logs = remember {mutableStateListOf<String>()}
            Column (modifier = Modifier
                .fillMaxWidth()
                .padding(50.dp, 30.dp, 0.dp, 50.dp)) {

                Text("Logs", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Color.DarkGray, modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 30.dp))

//              https://rapidapi.com/petapro/api/linguatools-sentence-generating
                LazyColumn(modifier = Modifier
                    .padding(0.dp, 0.dp, 50.dp, 0.dp)
                    .fillMaxHeight(0.9f)) {
                    items(logs) {
                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                        TextButton(onClick = {}){Text("${Calendar.getInstance().time} | $it")}
                    }
                }
                Row  (horizontalArrangement = Arrangement.spacedBy(15.dp)){

                    Button(onClick = {
                        val queue = Volley.newRequestQueue(context)
                        val url = "https://baconipsum.com/api/?type=all-meat&sentences=1"

                        val stringRequest = StringRequest(Request.Method.GET, url,
                            { response ->
                                logs.add("Initialize HTTP Request to FortiGate Server")
                                logs.add("Got magic ID")
                                logs.add("Sending magic ID to FortiGate port 1003")
                                logs.add("Received connection end, checking network access")
                                logs.add("200 OK CONNECT")
                                println(logs)
                            },
                            { Toast.makeText(context, "That didn't work!", Toast.LENGTH_LONG).show()}
                        )

                        queue.add(stringRequest)
                    }) {
                        Text("Add log")
                    }
                    Button(onClick = {logs.clear()}) {
                        Text("Clear logs")
                    }
                }
            }
    }
}
