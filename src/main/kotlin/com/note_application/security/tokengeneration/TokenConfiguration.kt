package com.note_application.security.tokengeneration

class TokenConfiguration (
    val issuer: String,
    val targetedAudience: String,
    val tokenExpiresIn: Long,
    val tokenSecret: String
        ) {
}