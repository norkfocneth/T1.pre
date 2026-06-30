package com.example.t1.data.remote

import com.example.t1.data.remote.model.ProfileDto
import com.example.t1.data.remote.model.LeaderboardEntryDto
import com.example.t1.data.remote.model.DailyFocusScoreDto
import com.example.t1.data.remote.model.LeaderboardDailyDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseService @Inject constructor(
    val client: SupabaseClient
) {
    val auth = client.auth
    val postgrest = client.postgrest

    suspend fun getProfile(userId: String): ProfileDto? {
        return try {
            val response = postgrest.from("profiles")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("id", userId)
                    }
                }
            response.decodeList<ProfileDto>().firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun upsertProfile(profile: ProfileDto): Boolean {
        return try {
            postgrest.from("profiles").upsert(profile)
            true
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error upserting profile: ${e.message}", e)
            false
        }
    }

    suspend fun isUsernameTaken(usernameLower: String): Boolean {
        return try {
            val response = postgrest.from("profiles")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("username_lower", usernameLower)
                    }
                }
            // If the query returns any profiles with this username_lower, it's taken
            response.decodeList<ProfileDto>().isNotEmpty()
        } catch (e: Exception) {
            android.util.Log.w("SupabaseService", "Error checking username: ${e.message}. Assuming available.", e)
            false
        }
    }

    suspend fun getLeaderboard(): List<LeaderboardEntryDto> {
        return try {
            val response = postgrest.from("profiles")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("onboarding_completed", true)
                    }
                }
            response.decodeList<LeaderboardEntryDto>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllProfiles(): List<ProfileDto> {
        return try {
            val response = postgrest.from("profiles")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("onboarding_completed", true)
                    }
                }
            response.decodeList<ProfileDto>()
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error fetching all profiles: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getDailyLeaderboard(date: String): List<LeaderboardDailyDto> {
        return try {
            val response = postgrest.from("leaderboard_daily")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("snapshot_date", date)
                    }
                }
            response.decodeList<LeaderboardDailyDto>()
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error fetching daily leaderboard for date $date: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun upsertDailyLeaderboard(entries: List<LeaderboardDailyDto>): Boolean {
        return try {
            postgrest.from("leaderboard_daily").upsert(entries)
            true
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error upserting daily leaderboard: ${e.message}", e)
            false
        }
    }

    suspend fun upsertDailyFocusScore(score: DailyFocusScoreDto): Boolean {
        return try {
            postgrest.from("daily_focus_scores").upsert(score)
            true
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error upserting daily focus score: ${e.message}", e)
            false
        }
    }

    suspend fun getDailyFocusScore(userId: String, date: String): DailyFocusScoreDto? {
        return try {
            val response = postgrest.from("daily_focus_scores")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("user_id", userId)
                        eq("date", date)
                    }
                }
            response.decodeList<DailyFocusScoreDto>().firstOrNull()
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error fetching daily focus score: ${e.message}", e)
            null
        }
    }

    suspend fun getAllDailyFocusScores(userId: String): List<DailyFocusScoreDto> {
        return try {
            val response = postgrest.from("daily_focus_scores")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("user_id", userId)
                    }
                }
            response.decodeList<DailyFocusScoreDto>()
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error fetching all daily focus scores: ${e.message}", e)
            emptyList()
        }
    }
}
