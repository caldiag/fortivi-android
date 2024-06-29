package com.gruporihappy.fortivi.viewmodel.logs

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel

class CredentialsViewModel(application: Application) : AndroidViewModel(application) {
    var username = mutableStateOf<String>("")
    var password = mutableStateOf<String>("")
}