package com.example.assignmentclimbax.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.assignmentclimbax.domain.usecase.LoginUseCase
import com.example.assignmentclimbax.presentation.common.Resource
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _loginState = MutableLiveData<Resource<Int>>()
    val loginState: LiveData<Resource<Int>> = _loginState

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = Resource.Error("Username and password are required")
            return
        }
        _loginState.value = Resource.Loading
        viewModelScope.launch {
            loginUseCase(username, password)
                .onSuccess { _loginState.postValue(Resource.Success(it)) }
                .onFailure { _loginState.postValue(Resource.Error(it.message ?: "Login failed")) }
        }
    }
}

class LoginViewModelFactory(
    private val loginUseCase: LoginUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(loginUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
