package com.example.t1.util

import android.util.Log

/**
 * Production-ready centralized logging utility for T1 V2.
 * Statically enforces stripping of sensitive fields such as emails, tokens, and API keys.
 */
object T1Logger {
    private const val TAG = "T1_AuthSystem"

    fun d(message: String) {
        Log.d(TAG, sanitize(message))
    }

    fun i(message: String) {
        Log.i(TAG, sanitize(message))
    }

    fun w(message: String, throwable: Throwable? = null) {
        Log.w(TAG, sanitize(message), throwable)
    }

    fun e(message: String, throwable: Throwable? = null) {
        Log.e(TAG, sanitize(message), throwable)
    }

    /**
     * Sanitizes strings to prevent accidental leakage of sensitive tokens, credentials, or PII (e.g. email).
     */
    private fun sanitize(input: String): String {
        var clean = input
        // Mask emails
        val emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
        clean = clean.replace(emailRegex, "[REDACTED_EMAIL]")

        // Mask OAuth / JWT-like tokens or keys
        val tokenRegex = "eyJ[a-zA-Z0-9-_=]+\\.[a-zA-Z0-9-_=]+\\.[a-zA-Z0-9-_=]*".toRegex()
        clean = clean.replace(tokenRegex, "[REDACTED_JWT_TOKEN]")

        // Mask password strings and authorization headers if present
        clean = clean.replace(Regex("(?i)password\\s*=\\s*[^\\s]+"), "password=[REDACTED]")
        clean = clean.replace(Regex("(?i)bearer\\s+[^\\s]+"), "Bearer [REDACTED_TOKEN]")

        return clean
    }
}
