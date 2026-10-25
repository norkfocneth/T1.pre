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

import com.example.t1.data.remote.model.TokenWalletDto
import com.example.t1.data.remote.model.TokenTransactionDto
import com.example.t1.data.remote.model.FocusFightDto

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

    suspend fun getTokenWallet(userId: String): TokenWalletDto? {
        return try {
            val response = postgrest.from("token_wallets")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("user_id", userId)
                    }
                }
            response.decodeList<TokenWalletDto>().firstOrNull()
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error fetching token wallet: ${e.message}", e)
            null
        }
    }

    suspend fun upsertTokenWallet(wallet: TokenWalletDto): Boolean {
        return try {
            postgrest.from("token_wallets").upsert(wallet)
            true
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error upserting token wallet: ${e.message}", e)
            false
        }
    }

    suspend fun getTokenTransactions(userId: String): List<TokenTransactionDto> {
        return try {
            val response = postgrest.from("token_transactions")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("user_id", userId)
                    }
                }
            response.decodeList<TokenTransactionDto>()
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error fetching token transactions: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun insertTokenTransaction(tx: TokenTransactionDto): Boolean {
        return try {
            postgrest.from("token_transactions").insert(tx)
            true
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error inserting token transaction: ${e.message}", e)
            false
        }
    }

    suspend fun insertTokenTransactionsBatch(txs: List<TokenTransactionDto>): Boolean {
        if (txs.isEmpty()) return true
        return try {
            postgrest.from("token_transactions").insert(txs)
            true
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error inserting batch token transactions: ${e.message}", e)
            false
        }
    }

    suspend fun recordFocusFight(fight: FocusFightDto): Boolean {
        return try {
            postgrest.from("focus_fights").insert(fight)
            true
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error recording focus fight: ${e.message}", e)
            false
        }
    }

    suspend fun createFocusFight(fight: FocusFightDto): FocusFightDto? {
        return try {
            val response = postgrest.from("focus_fights").insert(fight) {
                select()
            }
            response.decodeSingle<FocusFightDto>()
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error creating focus fight: ${e.message}", e)
            null
        }
    }

    suspend fun getFightById(fightId: String): FocusFightDto? {
        return try {
            val response = postgrest.from("focus_fights").select(columns = Columns.ALL) {
                filter {
                    eq("id", fightId)
                }
            }
            response.decodeList<FocusFightDto>().firstOrNull()
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error fetching fight by id: ${e.message}", e)
            null
        }
    }

    suspend fun getPendingFightForUser(userId: String): FocusFightDto? {
        return try {
            val response = postgrest.from("focus_fights").select(columns = Columns.ALL) {
                filter {
                    eq("opponent_id", userId)
                    eq("status", "PENDING")
                }
            }
            response.decodeList<FocusFightDto>().lastOrNull()
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error fetching pending fights: ${e.message}", e)
            null
        }
    }

    suspend fun updateFightStatus(
        fightId: String,
        status: String,
        winnerId: String? = null,
        challengerSeconds: Int? = null,
        opponentSeconds: Int? = null
    ): Boolean {
        return try {
            postgrest.from("focus_fights").update({
                set("status", status)
                if (winnerId != null) set("winner_id", winnerId)
                if (challengerSeconds != null) set("challenger_seconds", challengerSeconds)
                if (opponentSeconds != null) set("opponent_seconds", opponentSeconds)
            }) {
                filter {
                    eq("id", fightId)
                }
            }
            true
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error updating fight status: ${e.message}", e)
            false
        }
    }

    suspend fun updateFightProgress(fightId: String, isChallenger: Boolean, secondsElapsed: Int): Boolean {
        return try {
            val column = if (isChallenger) "challenger_seconds" else "opponent_seconds"
            postgrest.from("focus_fights").update({
                set(column, secondsElapsed)
            }) {
                filter {
                    eq("id", fightId)
                }
            }
            true
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error updating fight progress: ${e.message}", e)
            false
        }
    }

    suspend fun getUserFocusFights(userId: String): List<FocusFightDto> {
        return try {
            val response = postgrest.from("focus_fights")
                .select(columns = Columns.ALL) {
                    filter {
                        or {
                            eq("challenger_id", userId)
                            eq("opponent_id", userId)
                        }
                    }
                }
            response.decodeList<FocusFightDto>()
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error fetching focus fights: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getAllFocusFights(): List<FocusFightDto> {
        return try {
            val response = postgrest.from("focus_fights").select(columns = Columns.ALL)
            response.decodeList<FocusFightDto>()
        } catch (e: Exception) {
            android.util.Log.e("SupabaseService", "Error fetching all focus fights: ${e.message}", e)
            emptyList()
        }
    }
}
