import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object AuthFlowLogs {
    private val _workResult = MutableLiveData<MutableList<String>>()
    val workResult: LiveData<MutableList<String>> = _workResult

    fun read(): MutableList<String> {
        return _workResult.value ?: mutableListOf()
    }

    fun updateWorkResult(result: MutableList<String>) {
        _workResult.postValue(result)
    }
}
