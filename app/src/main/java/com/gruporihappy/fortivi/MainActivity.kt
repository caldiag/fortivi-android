package com.gruporihappy.fortivi

import android.content.Context
import android.os.Bundle
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
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.gruporihappy.fortivi.ui.theme.FortiviTheme

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
                    fontSize = 50.sp,
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
            Column (modifier = Modifier.fillMaxWidth()) {
                Text("Logs", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Color.DarkGray, modifier = Modifier.padding(50.dp, 30.dp, 0.dp, 50.dp))
                LazyColumn {

                }
            }
    }
}