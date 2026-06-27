package com.example.t1.domain.usage

import com.example.t1.domain.model.usage.DailyUsageData
import com.example.t1.domain.model.usage.ForegroundSession
import com.example.t1.domain.model.behaviour.DailyBehaviourSummary
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class UsageDataValidatorTest {

    @Test
    fun testValidUsageData() {
        val sessions = listOf(
            ForegroundSession("com.instagram.android", 1000L, 2000L, 1000L),
            ForegroundSession("com.whatsapp", 3000L, 5000L, 2000L)
        )
        val data = DailyUsageData(
            date = LocalDate.now(),
            totalScreenTimeMs = 3000L,
            unlockCount = 5,
            appOpenCount = 10,
            foregroundSessions = sessions,
            firstAppOpenTime = 1000L,
            lastAppCloseTime = 5000L,
            collectionTimestamp = System.currentTimeMillis(),
            isVerified = true
        )
        assertTrue(UsageDataValidator.validateUsage(data) is ValidationResult.Valid)
    }

    @Test
    fun testInvalidUsageDataNegativeScreenTime() {
        val data = DailyUsageData(
            date = LocalDate.now(),
            totalScreenTimeMs = -500L,
            unlockCount = 5,
            appOpenCount = 10,
            foregroundSessions = emptyList(),
            firstAppOpenTime = null,
            lastAppCloseTime = null,
            collectionTimestamp = System.currentTimeMillis(),
            isVerified = true
        )
        assertTrue(UsageDataValidator.validateUsage(data) is ValidationResult.Invalid)
    }

    @Test
    fun testInvalidUsageDataInvalidSessionTimes() {
        val sessions = listOf(
            ForegroundSession("com.instagram.android", 2000L, 1000L, -1000L)
        )
        val data = DailyUsageData(
            date = LocalDate.now(),
            totalScreenTimeMs = 1000L,
            unlockCount = 5,
            appOpenCount = 10,
            foregroundSessions = sessions,
            firstAppOpenTime = 1000L,
            lastAppCloseTime = 2000L,
            collectionTimestamp = System.currentTimeMillis(),
            isVerified = true
        )
        assertTrue(UsageDataValidator.validateUsage(data) is ValidationResult.Invalid)
    }

    @Test
    fun testValidBehaviourSummary() {
        val behaviour = DailyBehaviourSummary(
            date = LocalDate.now(),
            totalScreenTimeMs = 5000L,
            socialTimeMs = 2000L,
            entertainmentTimeMs = 0L,
            productiveTimeMs = 3000L,
            educationTimeMs = 0L,
            communicationTimeMs = 0L,
            financeTimeMs = 0L,
            healthTimeMs = 0L,
            developmentTimeMs = 0L,
            otherTimeMs = 0L,
            unlockCount = 5,
            appOpenCount = 10,
            foregroundSessionCount = 2,
            topUsedApps = emptyList(),
            collectionTimestamp = System.currentTimeMillis()
        )
        assertTrue(UsageDataValidator.validateBehaviour(behaviour) is ValidationResult.Valid)
    }

    @Test
    fun testInvalidBehaviourSummaryNegativeTime() {
        val behaviour = DailyBehaviourSummary(
            date = LocalDate.now(),
            totalScreenTimeMs = 5000L,
            socialTimeMs = -2000L,
            entertainmentTimeMs = 0L,
            productiveTimeMs = 3000L,
            educationTimeMs = 0L,
            communicationTimeMs = 0L,
            financeTimeMs = 0L,
            healthTimeMs = 0L,
            developmentTimeMs = 0L,
            otherTimeMs = 0L,
            unlockCount = 5,
            appOpenCount = 10,
            foregroundSessionCount = 2,
            topUsedApps = emptyList(),
            collectionTimestamp = System.currentTimeMillis()
        )
        assertTrue(UsageDataValidator.validateBehaviour(behaviour) is ValidationResult.Invalid)
    }
}
