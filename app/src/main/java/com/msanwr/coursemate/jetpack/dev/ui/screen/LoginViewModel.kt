package com.msanwr.coursemate.jetpack.dev.ui.screen


import androidx.lifecycle.ViewModel
import com.msanwr.coursemate.jetpack.dev.auth.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val isLoading: Boolean = false
)

class LoginViewModel: ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage,
            isLoading = false
        )}
    }
    fun inSignInClick(){
        _state.update { it.copy(isLoading = true) }
    }
    fun resetState() {
        _state.update { LoginState() }
    }
}