package com.note_application.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationResponse (
    val token:String,
    val userId:String
) {
}