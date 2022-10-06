package com.note_application.security.tokengeneration

interface TokenGeneration {
    fun generateToken(tokenConfiguration:TokenConfiguration,
                      vararg tokenClaims:TokenClaims):String

}