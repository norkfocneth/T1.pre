package com.example.t1.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyFocusScoreDto(
    @SerialName("user_id")
    val userId: String,
    @SerialName("date")
    val date: String,
    @SerialName("questionnaire_score")
    val questionnaireScore: Int,
    @SerialName("behaviour_score")
    val behaviourScore: Int,
    @SerialName("confidence")
    val confidence: Int,
    @SerialName("final_focus_score")
    val finalFocusScore: Int,
    @SerialName("trend")
    val trend: String,
    @SerialName("time_saved")
    val timeSaved: Long,
    @SerialName("generated_at")
    val generatedAt: String, // ISO-8601 timestamp string
    @SerialName("engine_version")
    val engineVersion: String,
    @SerialName("verified")
    val verified: Boolean
)
