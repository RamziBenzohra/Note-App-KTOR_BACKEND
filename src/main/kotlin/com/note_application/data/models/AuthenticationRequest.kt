package com.note_application.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationRequest (
    val username:String,
    val password:String
        ){

}