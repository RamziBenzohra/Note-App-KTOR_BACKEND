package com.note_application.security.passwordHashing

interface Hashing {
     suspend fun buildSaltedHash(passwordFromUserInput: String, hashedSaltLength: Int = 32):SaltAndPasswordHashed
     suspend fun verifySaltedHash(passwordFromUserInput: String, saltAndHash: SaltAndPasswordHashed):Boolean
}