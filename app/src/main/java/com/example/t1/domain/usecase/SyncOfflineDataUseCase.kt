package com.example.t1.domain.usecase

import com.example.t1.domain.repository.UserRepository
import javax.inject.Inject

class SyncOfflineDataUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return userRepository.syncPendingEdits()
    }
}
