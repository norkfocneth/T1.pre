package com.example.t1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.t1.data.database.dao.AppCategoryOverrideDao
import com.example.t1.data.database.dao.DailyFocusScoreDao
import com.example.t1.data.database.entity.AppCategoryOverrideEntity
import com.example.t1.data.database.entity.DailyFocusScoreEntity
import com.example.t1.domain.focus.FocusScoreEngine
import com.example.t1.domain.model.UserProfile
import com.example.t1.domain.repository.BehaviourRepository
import com.example.t1.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
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

data class OnboardingAnalysisData(
    val initialScore: Int = 50,
    val avgScreenTimeFormatted: String = "4.2h",
    val topCategory: String = "Social"
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appCategoryOverrideDao: AppCategoryOverrideDao,
    private val behaviourRepository: BehaviourRepository,
    private val dailyFocusScoreDao: DailyFocusScoreDao
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
            try {
                val fullUsername = "${trimmed.lowercase()}.t1"
                val isTaken = userRepository.isUsernameTaken(fullUsername)
                if (isTaken) {
                    _usernameStatus.value = UsernameStatus.TAKEN
                } else {
                    _usernameStatus.value = UsernameStatus.AVAILABLE
                }
            } catch (e: Exception) {
                com.example.t1.util.T1Logger.e("Error checking username availability", e)
                _usernameStatus.value = UsernameStatus.ERROR
            }
            _suggestions.value = emptyList()
        }
    }

    suspend fun analyze7DaysUsage(
        primaryGoal: String,
        customOverrides: Map<String, String>
    ): OnboardingAnalysisData {
        return try {
            // Save category overrides first so behavior queries respect them
            val overrideEntities = customOverrides.map { (pkg, cat) ->
                AppCategoryOverrideEntity(packageName = pkg, category = cat)
            }
            if (overrideEntities.isNotEmpty()) {
                appCategoryOverrideDao.saveOverrides(overrideEntities)
            }

            val today = LocalDate.now()
            val past7Days = (1..7).map { today.minusDays(it.toLong()) }
            val summaries = past7Days.mapNotNull { date ->
                behaviourRepository.getBehaviourForDate(date).getOrNull()
            }

            if (summaries.isNotEmpty()) {
                val totalScreenTimeAvg = summaries.map { it.totalScreenTimeMs }.average().toLong()
                val hours = totalScreenTimeAvg / 3600000.0
                val formattedScreenTime = String.format(Locale.US, "%.1fh", hours)

                val avgSocial = summaries.map { it.socialTimeMs }.average()
                val avgProd = summaries.map { it.productiveTimeMs }.average()
                val avgEdu = summaries.map { it.educationTimeMs }.average()
                val avgEnt = summaries.map { it.entertainmentTimeMs }.average()
                val avgComm = summaries.map { it.communicationTimeMs }.average()

                val categoryAverages = mapOf(
                    "Social" to avgSocial,
                    "Productivity" to avgProd,
                    "Education" to avgEdu,
                    "Entertainment" to avgEnt,
                    "Communication" to avgComm
                )
                val topCat = categoryAverages.maxByOrNull { it.value }?.key ?: "Social"

                val calculatedScore = FocusScoreEngine.calculateInitialScoreFrom7Days(
                    dailySummaries = summaries,
                    goalCategory = primaryGoal
                )

                OnboardingAnalysisData(
                    initialScore = calculatedScore,
                    avgScreenTimeFormatted = formattedScreenTime,
                    topCategory = topCat
                )
            } else {
                OnboardingAnalysisData()
            }
        } catch (e: Exception) {
            com.example.t1.util.T1Logger.e("Error analyzing 7 days usage in onboarding", e)
            OnboardingAnalysisData()
        }
    }

    fun completeOnboarding(
        userId: String,
        username: String,
        displayName: String?,
        focusScore: Int,
        primaryGoal: String,
        customOverrides: Map<String, String>
    ) {
        _uiState.value = OnboardingUiState.Loading
        viewModelScope.launch {
            try {
                // 1. Save App Category Overrides
                val overrideEntities = customOverrides.map { (pkg, cat) ->
                    AppCategoryOverrideEntity(packageName = pkg, category = cat)
                }
                if (overrideEntities.isNotEmpty()) {
                    appCategoryOverrideDao.saveOverrides(overrideEntities)
                }

                // 2. Save UserProfile
                val profile = UserProfile(
                    id = userId,
                    username = username,
                    usernameLower = username.lowercase(),
                    displayName = displayName,
                    focusScore = focusScore,
                    primaryGoal = primaryGoal,
                    onboardingCompleted = true,
                    createdAt = System.currentTimeMillis()
                )
                val result = userRepository.saveProfile(profile)
                if (result.isSuccess) {
                    // 3. Seed Initial DailyFocusScoreEntity so Dashboard starts directly with real score
                    val todayStr = LocalDate.now().toString()
                    val initialEntity = DailyFocusScoreEntity(
                        userId = userId,
                        date = todayStr,
                        questionnaireScore = focusScore,
                        behaviourScore = focusScore,
                        confidence = 50,
                        finalFocusScore = focusScore,
                        trend = "Stable",
                        timeSaved = 0L,
                        generatedAt = System.currentTimeMillis(),
                        version = FocusScoreEngine.ENGINE_VERSION.toString(),
                        verified = true,
                        synced = false
                    )
                    dailyFocusScoreDao.insertOrReplace(initialEntity)

                    _uiState.value = OnboardingUiState.Success
                } else {
                    val error = result.exceptionOrNull()
                    _uiState.value = OnboardingUiState.Error(error?.message ?: "Failed to save profile")
                }
            } catch (e: Exception) {
                _uiState.value = OnboardingUiState.Error(e.message ?: "Failed to save profile")
            }
        }
    }

    fun resetState() {
        _uiState.value = OnboardingUiState.Idle
    }
}
