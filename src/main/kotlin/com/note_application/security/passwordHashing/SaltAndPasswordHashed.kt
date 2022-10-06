package com.note_application.security.passwordHashing

data class SaltAndPasswordHashed(
    val hash:String,
    val salt:String
)
