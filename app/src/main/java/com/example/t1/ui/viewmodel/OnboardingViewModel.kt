package com.example.t1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.t1.domain.repository.UserRepository
import com.example.t1.domain.model.UserProfile
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

/**
 * OnboardingViewModel stubbed for Phase 1.
 * Acts as a placeholder to allow UI compilation.
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository
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
            // Mock username check for Phase 1
            _usernameStatus.value = UsernameStatus.AVAILABLE
            _suggestions.value = emptyList()
        }
    }

    fun completeOnboarding(userId: String, username: String, displayName: String?, focusScore: Int) {
        _uiState.value = OnboardingUiState.Loading
        viewModelScope.launch {
            try {
                // Mock profile creation in cache to allow testing Phase 2 transition entry
                val mockProfile = UserProfile(
                    id = userId,
                    username = username,
                    usernameLower = username.lowercase(),
                    displayName = displayName,
                    focusScore = focusScore,
                    onboardingCompleted = true,
                    createdAt = System.currentTimeMillis()
                )
                userRepository.saveCachedProfile(mockProfile)
                _uiState.value = OnboardingUiState.Success
            } catch (e: Exception) {
                _uiState.value = OnboardingUiState.Error(e.message ?: "Failed to save profile")
            }
        }
    }

    fun resetState() {
        _uiState.value = OnboardingUiState.Idle
    }
}
