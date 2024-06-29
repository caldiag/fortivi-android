package com.gruporihappy.fortivi.home.components

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gruporihappy.fortivi.auth.logic.ConnectionManagerService
import com.gruporihappy.fortivi.viewmodel.logs.LogsViewModel

@Composable
fun Home(context: Context, prefs: SharedPreferences) {
    var username by remember { mutableStateOf(prefs.getString("username", "") ?: "") }
    var password by remember { mutableStateOf(prefs.getString("password", "") ?: "") }
    var hasUsername by remember { mutableStateOf(prefs.contains("username")) }
    var hasPassword by remember { mutableStateOf(prefs.contains("password")) }
    val editor = prefs.edit()
    val logsViewModel = viewModel<LogsViewModel>()
    val logsFlow = logsViewModel.read().collectAsStateWithLifecycle()

    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        // Login Column
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(50.dp, 30.dp)
                .weight(1f)
        ) {
            Text(
                "FortiGate Login",
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 20.dp)
            )
            TextField(
                keyboardOptions = KeyboardOptions(autoCorrect = false),
                value = username,
                enabled = !hasUsername,
                onValueChange = { username = it.lowercase() },
                label = { Text("Username") }
            )
            TextField(
                keyboardOptions = KeyboardOptions(autoCorrect = false),
                value = password,
                enabled = !hasUsername,
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = { password = it },
                label = { Text("Password") },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Button(onClick = {
                    if (username == "" || password == "") {
                        Toast.makeText(context, "You need to input a username and a password.", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    Intent(context, ConnectionManagerService::class.java).also {
                        it.action = ConnectionManagerService.Actions.START.toString()
                        context.startService(it)
                    }
                }, Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp)) {
                    Text("Start")
                }
                FilledTonalButton(onClick = {
                    if (username == "" || password == "") {
                        Toast.makeText(context, "You need to input a username and a password.", Toast.LENGTH_LONG).show()
                        return@FilledTonalButton
                    }

                    Intent(context, ConnectionManagerService::class.java).also {
                        it.action = ConnectionManagerService.Actions.STOP.toString()
                        context.startService(it)
                    }
                }, Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp)) {
                    Text("Stop")
                }
                FilledTonalButton(onClick = {
                    if (username == "" || password == "") {
                        Toast.makeText(context, "You need to input a username and a password.", Toast.LENGTH_LONG).show()
                        return@FilledTonalButton
                    }
                    editor.putString("username", username)
                    editor.putString("password", password)
                    editor.commit()

                    hasUsername = true
                    hasPassword = true
                }, Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp)) {
                    Text("Save credentials")
                }
                FilledTonalButton(onClick = {
                    editor.putString("username", "")
                    editor.putString("password", "")
                    editor.commit()

                    hasUsername = false
                    hasPassword = false
                }, Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp)) {
                    Text("Clear")
                }
            }
        }

        VerticalDivider(
            color = Color.Gray,
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
        )

        // Logs Column
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(50.dp, 30.dp, 0.dp, 50.dp)
        ) {
            Text("Logs", fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 30.dp))
            LazyColumn(
                modifier = Modifier
                    .padding(0.dp, 0.dp, 50.dp, 0.dp)
                    .fillMaxHeight(0.9f)
            ) {
                items(logsFlow.value) {
                    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                    TextButton(onClick = {}) { Text(it) }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                Button(onClick = { logsViewModel.clear() }) {
                    Text("Clear logs")
                }
            }
        }
    }
}