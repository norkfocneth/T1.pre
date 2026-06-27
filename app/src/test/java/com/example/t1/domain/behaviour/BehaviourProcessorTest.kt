package com.example.t1.domain.behaviour

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.t1.domain.model.behaviour.AppCategory
import com.example.t1.domain.model.usage.DailyUsageData
import com.example.t1.domain.model.usage.ForegroundSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.time.LocalDate

class BehaviourProcessorTest {

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var packageManager: PackageManager

    @Mock
    private lateinit var applicationInfo: ApplicationInfo

    private lateinit var appCategoryEngine: AppCategoryEngine
    private lateinit var behaviourProcessor: BehaviourProcessor

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(context.packageManager).thenReturn(packageManager)
        `when`(packageManager.getApplicationInfo(anyString(), anyInt())).thenReturn(applicationInfo)
        `when`(packageManager.getApplicationLabel(applicationInfo)).thenReturn("Test App")

        appCategoryEngine = AppCategoryEngine()
        behaviourProcessor = BehaviourProcessor(context, appCategoryEngine)
    }

    @Test
    fun testProcessUsageDataToBehaviourSummary() {
        val sessions = listOf(
            ForegroundSession("com.instagram.android", 1000L, 3000L, 2000L), // SOCIAL
            ForegroundSession("com.notion.org", 4000L, 9000L, 5000L),      // PRODUCTIVITY
            ForegroundSession("com.whatsapp", 10000L, 11000L, 1000L)       // COMMUNICATION
        )

        val usageData = DailyUsageData(
            date = LocalDate.now(),
            totalScreenTimeMs = 8000L,
            unlockCount = 3,
            appOpenCount = 3,
            foregroundSessions = sessions,
            firstAppOpenTime = 1000L,
            lastAppCloseTime = 11000L,
            collectionTimestamp = System.currentTimeMillis(),
            isVerified = true
        )

        val summary = behaviourProcessor.process(usageData)

        assertEquals(8000L, summary.totalScreenTimeMs)
        assertEquals(2000L, summary.socialTimeMs)
        assertEquals(5000L, summary.productiveTimeMs)
        assertEquals(1000L, summary.communicationTimeMs)
        assertEquals(0L, summary.entertainmentTimeMs)
        assertEquals(3, summary.unlockCount)
        assertEquals(3, summary.appOpenCount)
        assertEquals(3, summary.foregroundSessionCount)
        assertTrue(summary.topUsedApps.isNotEmpty())
        assertEquals("com.notion.org", summary.topUsedApps[0].packageName)
    }
}
