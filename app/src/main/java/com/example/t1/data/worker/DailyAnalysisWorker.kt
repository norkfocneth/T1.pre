package com.example.t1.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.t1.domain.repository.AuthRepository
import com.example.t1.domain.repository.FocusScoreRepository
import com.example.t1.domain.repository.LeaderboardRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

@HiltWorker
class DailyAnalysisWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val authRepository: AuthRepository,
    private val focusScoreRepository: FocusScoreRepository,
    private val leaderboardRepository: LeaderboardRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // 1. Check if user is authenticated
        val userId = authRepository.currentUserIdSync
        if (userId == null) {
            android.util.Log.w("DailyAnalysisWorker", "User not authenticated. Aborting daily analysis.")
            return Result.failure()
        }

        // 2. Determine target date (default to yesterday)
        val dateStr = inputData.getString("date") ?: LocalDate.now().minusDays(1).toString()

        android.util.Log.i("DailyAnalysisWorker", "Starting daily analysis for user: $userId on date: $dateStr")

        // 3. Run analysis
        val result = focusScoreRepository.calculateAndSaveFocusScore(dateStr)

        return if (result.isSuccess) {
            android.util.Log.i("DailyAnalysisWorker", "Daily analysis succeeded for date: $dateStr. Recalculating daily snapshot.")
            val leaderboardResult = leaderboardRepository.generateDailySnapshot(dateStr)
            if (leaderboardResult.isSuccess) {
                Result.success()
            } else {
                android.util.Log.e("DailyAnalysisWorker", "Leaderboard snapshot generation failed: ${leaderboardResult.exceptionOrNull()?.message}")
                Result.retry()
            }
        } else {
            val exception = result.exceptionOrNull()
            android.util.Log.e("DailyAnalysisWorker", "Daily analysis failed: ${exception?.message}", exception)
            Result.retry()
        }
    }
}
