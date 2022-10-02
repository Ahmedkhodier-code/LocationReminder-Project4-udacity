package com.udacity.project4.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map


class LoginViewModel : ViewModel() {
    private val _username = MutableLiveData<String?>()
    val username: LiveData<String?>
        get() = _username

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
//            _username.value = user.displayName
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

}