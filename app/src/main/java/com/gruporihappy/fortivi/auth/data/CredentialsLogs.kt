import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object CredentialsLogs {
    private val _username = MutableLiveData("")
    val username: LiveData<String> = _username
    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    fun readUsername(): String {
        return _username.value ?: ""
    }

    fun updateUsername(newUsername: String) {
        _username.postValue(newUsername)
    }

    fun readPassword(): String {
        return _password.value ?: ""
    }

    fun updatePassword(newPassword: String) {
        _password.postValue(newPassword)
    }
}
