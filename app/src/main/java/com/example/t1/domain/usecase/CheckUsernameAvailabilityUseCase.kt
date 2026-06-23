package com.example.t1.domain.usecase

import com.example.t1.domain.repository.UserRepository
import javax.inject.Inject

class CheckUsernameAvailabilityUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String): Result<Boolean> {
        return userRepository.checkUsernameAvailable(username)
    }
}
