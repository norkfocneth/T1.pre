package com.example.t1.domain.usage

import com.example.t1.domain.model.usage.DailyUsageData
import com.example.t1.domain.model.behaviour.DailyBehaviourSummary

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}

object UsageDataValidator {
    fun validateUsage(data: DailyUsageData): ValidationResult {
        if (data.totalScreenTimeMs < 0) {
            return ValidationResult.Invalid("Negative screen time: ${data.totalScreenTimeMs}")
        }
        if (data.unlockCount < 0) {
            return ValidationResult.Invalid("Negative unlock count: ${data.unlockCount}")
        }
        if (data.appOpenCount < 0) {
            return ValidationResult.Invalid("Negative app open count: ${data.appOpenCount}")
        }
        if (data.collectionTimestamp > System.currentTimeMillis() + 600000) { // Allow up to 10 mins system clock skew
            return ValidationResult.Invalid("Collection timestamp in future: ${data.collectionTimestamp}")
        }
        for (session in data.foregroundSessions) {
            if (session.durationMs <= 0) {
                return ValidationResult.Invalid("Non-positive session duration: ${session.durationMs} for package ${session.packageName}")
            }
            if (session.startTime > session.endTime) {
                return ValidationResult.Invalid("Session start time ${session.startTime} after end time ${session.endTime} for package ${session.packageName}")
            }
        }
        return ValidationResult.Valid
    }

    fun validateBehaviour(summary: DailyBehaviourSummary): ValidationResult {
        if (summary.totalScreenTimeMs < 0) {
            return ValidationResult.Invalid("Negative screen time: ${summary.totalScreenTimeMs}")
        }
        if (summary.socialTimeMs < 0 || summary.entertainmentTimeMs < 0 ||
            summary.productiveTimeMs < 0 || summary.educationTimeMs < 0 ||
            summary.communicationTimeMs < 0 || summary.financeTimeMs < 0 ||
            summary.healthTimeMs < 0 || summary.developmentTimeMs < 0 ||
            summary.otherTimeMs < 0
        ) {
            return ValidationResult.Invalid("Negative category time in behaviour summary")
        }
        if (summary.unlockCount < 0) {
            return ValidationResult.Invalid("Negative unlock count: ${summary.unlockCount}")
        }
        if (summary.appOpenCount < 0) {
            return ValidationResult.Invalid("Negative app open count: ${summary.appOpenCount}")
        }
        if (summary.collectionTimestamp > System.currentTimeMillis() + 600000) {
            return ValidationResult.Invalid("Collection timestamp in future: ${summary.collectionTimestamp}")
        }
        return ValidationResult.Valid
    }
}
