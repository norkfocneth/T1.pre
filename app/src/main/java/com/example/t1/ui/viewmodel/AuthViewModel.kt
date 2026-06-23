package com.example.t1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.t1.domain.usecase.SignInWithEmailUseCase
import com.example.t1.domain.usecase.SignUpWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val userId: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    private val signUpWithEmailUseCase: SignUpWithEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, plainPassword: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            signInWithEmailUseCase(email, plainPassword)
                .onSuccess { userId ->
                    _uiState.value = AuthUiState.Success(userId)
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception.localizedMessage ?: "Sign in failed")
                }
        }
    }

    fun signUp(email: String, plainPassword: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            signUpWithEmailUseCase(email, plainPassword)
                .onSuccess { userId ->
                    _uiState.value = AuthUiState.Success(userId)
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception.localizedMessage ?: "Sign up failed")
                }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
