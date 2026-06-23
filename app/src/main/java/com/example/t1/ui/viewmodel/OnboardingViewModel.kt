package com.example.t1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.t1.domain.usecase.CheckUsernameAvailabilityUseCase
import com.example.t1.domain.usecase.SaveOnboardingAnswersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class UsernameStatus {
    IDLE, CHECKING, AVAILABLE, TAKEN, ERROR
}

sealed interface OnboardingUiState {
    object Idle : OnboardingUiState
    object Loading : OnboardingUiState
    object Success : OnboardingUiState
    data class Error(val message: String) : OnboardingUiState
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val checkUsernameAvailabilityUseCase: CheckUsernameAvailabilityUseCase,
    private val saveOnboardingAnswersUseCase: SaveOnboardingAnswersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Idle)
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _usernameStatus = MutableStateFlow(UsernameStatus.IDLE)
    val usernameStatus: StateFlow<UsernameStatus> = _usernameStatus.asStateFlow()

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    fun checkUsername(username: String) {
        val trimmed = username.trim()
        if (trimmed.length < 2) {
            _usernameStatus.value = UsernameStatus.IDLE
            _suggestions.value = emptyList()
            return
        }

        _usernameStatus.value = UsernameStatus.CHECKING
        viewModelScope.launch {
            checkUsernameAvailabilityUseCase(trimmed)
                .onSuccess { available ->
                    if (available) {
                        _usernameStatus.value = UsernameStatus.AVAILABLE
                        _suggestions.value = emptyList()
                    } else {
                        _usernameStatus.value = UsernameStatus.TAKEN
                        val lowercase = trimmed.lowercase()
                        _suggestions.value = listOf("_x", "2k", "_pro", ".go", "_hq").map { "$lowercase$it" }.take(3)
                    }
                }
                .onFailure {
                    // In case of error (e.g. offline checking), we fallback
                    _usernameStatus.value = UsernameStatus.ERROR
                    _suggestions.value = emptyList()
                }
        }
    }

    fun completeOnboarding(userId: String, username: String, displayName: String?, focusScore: Int) {
        _uiState.value = OnboardingUiState.Loading
        viewModelScope.launch {
            saveOnboardingAnswersUseCase(userId, username, displayName, focusScore)
                .onSuccess {
                    _uiState.value = OnboardingUiState.Success
                }
                .onFailure { exception ->
                    _uiState.value = OnboardingUiState.Error(exception.localizedMessage ?: "Failed to save profile")
                }
        }
    }

    fun resetState() {
        _uiState.value = OnboardingUiState.Idle
    }
}
