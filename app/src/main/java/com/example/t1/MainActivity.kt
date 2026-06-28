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

  override fun onCreate(savedInstanceState: Bundle?) {
    val splashScreen = installSplashScreen()

    super.onCreate(savedInstanceState)

    // Schedule the daily analysis worker
    DailyAnalysisScheduler.schedule(applicationContext)

    // Handle deep link on cold start
    T1Logger.i("MainActivity onCreate intent data: ${intent?.data}")
    intent?.let { supabaseClient.handleDeeplinks(it) }

    // Dismiss splash immediately — no keepOnScreen delay
    splashScreen.setKeepOnScreenCondition { false }

    enableEdgeToEdge()
    setContent {
      T1Theme { Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { MainNavigation() } }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    T1Logger.i("MainActivity onNewIntent intent data: ${intent.data}")
    supabaseClient.handleDeeplinks(intent)
  }
}
