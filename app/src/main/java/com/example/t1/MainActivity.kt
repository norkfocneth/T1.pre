package com.example.t1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.t1.theme.T1Theme
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import javax.inject.Inject
import android.content.Intent
import com.example.t1.util.T1Logger

import com.example.t1.data.worker.DailyAnalysisScheduler

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject
  lateinit var supabaseClient: SupabaseClient

  @Inject
  lateinit var t1NotificationManager: com.example.t1.notification.T1NotificationManager

  override fun onCreate(savedInstanceState: Bundle?) {
    val splashScreen = installSplashScreen()

    super.onCreate(savedInstanceState)

    // Schedule the daily analysis worker
    DailyAnalysisScheduler.schedule(applicationContext)
    com.example.t1.data.worker.TokenSyncWorker.enqueue(applicationContext)

    // Initialize haptics and sound settings
    com.example.t1.util.Haptics.init(applicationContext)

    // Initialize notification channels and scheduler
    t1NotificationManager.createNotificationChannels()
    com.example.t1.notification.T1NotificationScheduler.schedule(applicationContext)

    // Prompt for notification permission on Android 13+ (API 33+)
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
      if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
      }
    }

    // Handle deep link on cold start
    T1Logger.i("MainActivity onCreate intent data: ${intent?.data}")
    intent?.let { supabaseClient.handleDeeplinks(it) }

    // Dismiss splash immediately — no keepOnScreen delay
    splashScreen.setKeepOnScreenCondition { false }

    val previewScreen = intent?.getStringExtra("preview_screen") ?: if (intent?.getBooleanExtra("preview_onboarding", false) == true) "questions" else null

    enableEdgeToEdge()
    setContent {
      T1Theme { Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { MainNavigation(previewScreen = previewScreen) } }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    T1Logger.i("MainActivity onNewIntent intent data: ${intent.data}")
    supabaseClient.handleDeeplinks(intent)
  }
}
