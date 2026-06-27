package com.example.t1.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class SupabaseServiceTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Ignore("Requires Android environment or active emulator for Supabase Client Auth initialization")
    @Test
    fun testIsUsernameTaken() = runTest {
        try {
            val client = createSupabaseClient(
                supabaseUrl = "https://ehukatpgqeqjkcgqhxtr.supabase.co",
                supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVodWthdHBncWVxamtjZ3FoeHRyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODI1MDIwMjksImV4cCI6MjA5ODA3ODAyOX0.-YoeeLX4ZWn9YbeC-GVsV5yARR1HWnbqwraWBE1ZTsM"
            ) {
                install(io.github.jan.supabase.auth.Auth) {
                    sessionManager = io.github.jan.supabase.auth.MemorySessionManager()
                    codeVerifierCache = io.github.jan.supabase.auth.MemoryCodeVerifierCache()
                }
                install(Postgrest)
            }
            val service = SupabaseService(client)
            val result = service.isUsernameTaken("jjjjjjjj")
            println("TEST_OUTPUT_RESULT: $result")
        } catch (e: Throwable) {
            println("TEST_OUTPUT_EXCEPTION: ${e.message}")
        }
    }
}
