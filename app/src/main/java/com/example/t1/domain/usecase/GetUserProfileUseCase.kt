package com.example.t1.domain.usecase

import com.example.t1.domain.model.UserProfile
import com.example.t1.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, forceRefresh: Boolean = false): Result<UserProfile?> {
        return userRepository.getUserProfile(userId, forceRefresh)
    }
}
