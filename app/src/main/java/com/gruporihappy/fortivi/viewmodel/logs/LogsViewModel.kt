package com.gruporihappy.fortivi.viewmodel.logs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LogsViewModel : ViewModel() {
    private val _logs = MutableLiveData<MutableList<String>>(mutableListOf("Start"))
    val logs: LiveData<MutableList<String>> = _logs

    fun add(log: String, from: String) {
        println("from $from: \n$log")
        val currentLogs = _logs.value ?: mutableListOf()
        currentLogs.add(log)
        _logs.postValue(currentLogs)
    }

    fun read(): List<String>? {
        return logs.value
    }

    fun clear() {
        _logs.value?.clear()
        _logs.postValue(_logs.value)
    }
}
