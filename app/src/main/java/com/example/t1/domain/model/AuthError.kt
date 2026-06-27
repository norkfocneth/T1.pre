package com.example.t1.domain.model

/**
 * Represents structured categories of authentication errors.
 */
enum class AuthError {
    NETWORK_ERROR,
    CREDENTIAL_CANCELLED,
    SUPABASE_ERROR,
    SESSION_EXPIRED,
    SECURITY_FAILURE,
    TIMEOUT,
    UNKNOWN
}
