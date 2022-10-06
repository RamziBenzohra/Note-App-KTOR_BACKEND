package com.note_application.security.passwordHashing

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

class HashingImpl : Hashing {
    override suspend fun buildSaltedHash(passwordFromUserInput: String, hashedSaltLength: Int): SaltAndPasswordHashed {
        val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(hashedSaltLength)
        val saltToHexSalt= Hex.encodeHexString(salt)
        val completeHash = DigestUtils.sha256Hex("$saltToHexSalt$passwordFromUserInput")
        return SaltAndPasswordHashed(hash = completeHash, salt = saltToHexSalt)
    }

    override suspend fun verifySaltedHash(passwordFromUserInput: String, saltAndHash: SaltAndPasswordHashed): Boolean {
        return DigestUtils.sha256Hex(saltAndHash.salt + passwordFromUserInput) == saltAndHash.hash
    }
}