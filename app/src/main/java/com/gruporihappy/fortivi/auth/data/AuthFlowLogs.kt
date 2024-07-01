import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object AuthFlowLogs {
    private val _logsList = MutableLiveData<MutableList<String>>()
    val logsList: LiveData<MutableList<String>> = _logsList

    private val _isRunning = MutableLiveData<MutableState<Boolean>>()
    val isRunning: LiveData<MutableState<Boolean>> = _isRunning

    private val _isAuthenticating = MutableLiveData<MutableState<Boolean>>()
    val isAuthenticating: LiveData<MutableState<Boolean>> = _isRunning

    fun read(): MutableList<String> {
        return _logsList.value ?: mutableListOf()
    }

    fun updateWorkResult(result: MutableList<String>) {
        _logsList.postValue(result)
    }

    fun readIsServiceRunning(): MutableState<Boolean> {
        return _isRunning.value ?: mutableStateOf(false)
    }

    fun setIsServiceRunning(bool: Boolean) {
        _isRunning.postValue(mutableStateOf(bool))
    }

    fun readIsAuthenticating(): MutableState<Boolean> {
        return _isAuthenticating.value ?: mutableStateOf(false)
    }

    fun setIsAuthenticating(bool: Boolean) {
        _isAuthenticating.postValue(mutableStateOf(bool))
    }
}
