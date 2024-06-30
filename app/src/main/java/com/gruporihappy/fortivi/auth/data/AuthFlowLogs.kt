import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object AuthFlowLogs {
    private val _workResult = MutableLiveData<MutableList<String>>()
    val workResult: LiveData<MutableList<String>> = _workResult

    private val _isRunning = MutableLiveData<MutableState<Boolean>>()
    val isRunning: LiveData<MutableState<Boolean>> = _isRunning

    fun read(): MutableList<String> {
        return _workResult.value ?: mutableListOf()
    }

    fun updateWorkResult(result: MutableList<String>) {
        _workResult.postValue(result)
    }

    fun readIsRunning(): MutableState<Boolean> {
        return _isRunning.value ?: mutableStateOf(false)
    }

    fun setIsRunning(bool: Boolean) {
        _isRunning.postValue(mutableStateOf(bool))
    }
}
