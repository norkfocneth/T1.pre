package com.example.t1.data.remote

import android.content.Context
import com.example.t1.data.remote.AuthProvider
import com.example.t1.util.T1Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of AuthProvider using Supabase Browser-based Google OAuth.
 */
@Singleton
class GoogleAuthProvider @Inject constructor(
    private val supabaseClient: SupabaseClient
) : AuthProvider {

    override suspend fun signIn(context: Context): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Pre-check network connectivity to prevent ERR_ADDRESS_UNREACHABLE in browser
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager
            val network = connectivityManager?.activeNetwork
            val capabilities = connectivityManager?.getNetworkCapabilities(network)
            val isConnected = capabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

            if (!isConnected) {
                return@withContext Result.failure(java.io.IOException("No internet connection. Please check your Wi-Fi or mobile data."))
            }

            T1Logger.i("Starting Google Sign-In via Supabase OAuth browser flow")

            val url = supabaseClient.auth.getOAuthUrl(provider = Google, redirectUrl = "t1://login-callback")
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url)).apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)

            // Return a success placeholder; actual authentication completion is handled via deep link redirect
            Result.success("Browser launched")
        } catch (e: Exception) {
            T1Logger.e("Google OAuth browser flow launch failed", e)
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            T1Logger.i("Logging out of Supabase")
            supabaseClient.auth.signOut()
            T1Logger.i("Supabase logout completed successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            T1Logger.e("Failed to sign out from Supabase", e)
            Result.failure(e)
        }
    }
}
