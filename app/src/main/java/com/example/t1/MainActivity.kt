package com.example.t1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.t1.data.worker.DailyAnalysisScheduler
import com.example.t1.notification.FocusChallengeWatchdogReceiver
import com.example.t1.notification.T1NotificationManager
import com.example.t1.notification.T1NotificationScheduler
import com.example.t1.theme.T1Theme
import com.example.t1.util.Haptics
import com.example.t1.util.T1Logger
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject lateinit var supabaseClient: SupabaseClient
  @Inject lateinit var t1NotificationManager: T1NotificationManager

  override fun onCreate(savedInstanceState: Bundle?) {
    val splashScreen = installSplashScreen()

    super.onCreate(savedInstanceState)

    // Schedule the daily analysis worker
    DailyAnalysisScheduler.schedule(applicationContext)
    com.example.t1.data.worker.TokenSyncWorker.enqueue(applicationContext)

    // Initialize haptics and sound settings
    Haptics.init(applicationContext)

    // Initialize notification channels, scheduler, and OEM watchdog
    t1NotificationManager.createNotificationChannels()
    T1NotificationScheduler.schedule(applicationContext)
    FocusChallengeWatchdogReceiver.scheduleWatchdog(applicationContext, 45_000L)

    // Notification permission is requested gracefully during onboarding

    // Handle deep link on cold start
    T1Logger.i("MainActivity onCreate intent data: ${intent?.data}")
    intent?.let { supabaseClient.handleDeeplinks(it) }

    // Dismiss splash immediately — no keepOnScreen delay
    splashScreen.setKeepOnScreenCondition { false }

    val previewScreen = intent?.getStringExtra("preview_screen") 
      ?: if (intent?.getStringExtra("navigate_tab") == "BATTLE") "focus_fight" 
      else if (intent?.getBooleanExtra("preview_onboarding", false) == true) "questions" 
      else null

    val autoAcceptFightId = intent?.getStringExtra("auto_accept_fight_id")

    enableEdgeToEdge()
    setContent {
      T1Theme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          MainNavigation(previewScreen = previewScreen, autoAcceptFightId = autoAcceptFightId)
        }
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    T1Logger.i("MainActivity onNewIntent intent data: ${intent.data}")
    supabaseClient.handleDeeplinks(intent)
    val previewScreen = intent.getStringExtra("preview_screen") ?: if (intent.getStringExtra("navigate_tab") == "BATTLE") "focus_fight" else null
    val autoAcceptFightId = intent.getStringExtra("auto_accept_fight_id")
    if (previewScreen != null || autoAcceptFightId != null) {
      setContent {
        T1Theme {
          Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            MainNavigation(previewScreen = previewScreen, autoAcceptFightId = autoAcceptFightId)
          }
        }
      }
    }
  }

  override fun onStop() {
    super.onStop()
    T1NotificationScheduler.enqueueFastCheck(applicationContext)
    FocusChallengeWatchdogReceiver.scheduleWatchdog(applicationContext, 2000L)
  }
}
