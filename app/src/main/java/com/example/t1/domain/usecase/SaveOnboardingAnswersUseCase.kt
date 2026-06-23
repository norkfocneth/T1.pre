package com.example.t1.domain.usecase

import com.example.t1.domain.repository.UserRepository
import javax.inject.Inject

class SaveOnboardingAnswersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, username: String, displayName: String?, focusScore: Int): Result<Unit> {
        return userRepository.saveOnboardingProfile(userId, username, displayName, focusScore)
    }
}
