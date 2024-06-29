package com.gruporihappy.fortivi.viewmodel.logs

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class LogsViewModel : ViewModel() {
    private var logs = MutableStateFlow<MutableList<String>>(mutableListOf())
    fun read(): MutableStateFlow<MutableList<String>> {
        return logs
    }
    fun clear(){
        logs.value.clear()
    }
    fun add(log: String){
        logs.value.add(log)
    }
}