package com.example.t1.domain.usecase

import com.example.t1.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<String> {
        return authRepository.signInWithEmail(email.trim(), password)
    }
}
