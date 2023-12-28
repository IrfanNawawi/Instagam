package id.heycoding.storysubmission.ui.auth

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.heycoding.storysubmission.data.remote.WebServices
import id.heycoding.storysubmission.data.remote.response.auth.UserLoginResponse
import id.heycoding.storysubmission.data.remote.response.auth.UserLoginResult
import id.heycoding.storysubmission.data.remote.response.auth.UserRegisterResponse
import id.heycoding.storysubmission.utils.Helper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {

    private val _userLogin = MutableLiveData<UserLoginResult>()
    val userLogin: LiveData<UserLoginResult> = _userLogin

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val services = WebServices.create()

    fun validateCredentials(
        email: String, name: String, password: String,
        isLogin: Boolean
    ): Pair<Boolean, String> {
        var result = Pair(true, "")
        if (TextUtils.isEmpty(email) || (!isLogin && TextUtils.isEmpty(name)) || TextUtils.isEmpty(
                password
            )
        ) {
            result = Pair(false, "Please provide the credentials")
        } else if (!Helper.isValidEmail(email)) {
            result = Pair(false, "Email is invalid")
        } else if (!TextUtils.isEmpty(password) && password.length < 8) {
            result = Pair(false, "Password length should be greater than 8")
        }
        return result
    }

    fun doLogin(email: String, password: String) {
        _isLoading.value = true
        services.loginUser(email, password)
            .enqueue(object : Callback<UserLoginResponse> {
                override fun onResponse(
                    call: Call<UserLoginResponse>,
                    response: Response<UserLoginResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _isError.value = response.body()?.error
                        _message.value = response.body()?.message
                        _userLogin.value = response.body()?.loginResult
                    } else {
                        _message.value = response.message()
                    }
                }

                override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                    _message.value = t.message
                    _isLoading.value = false
                }
            })
    }

    fun doRegister(name: String, email: String, password: String) {
        _isLoading.value = true
        services.registerUser(name, email, password)
            .enqueue(object : Callback<UserRegisterResponse> {
                override fun onResponse(
                    call: Call<UserRegisterResponse>,
                    response: Response<UserRegisterResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _isError.value = response.body()?.error
                        _message.value = response.body()?.message
                    } else {
                        _message.value = response.message()
                    }
                }

                override fun onFailure(call: Call<UserRegisterResponse>, t: Throwable) {
                    _message.value = t.message
                    _isLoading.value = false
                }

            })
    }
}