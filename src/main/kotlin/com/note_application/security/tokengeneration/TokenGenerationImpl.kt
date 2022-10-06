package com.note_application.security.tokengeneration

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class TokenGenerationImpl : TokenGeneration {
    override fun generateToken(tokenConfiguration: TokenConfiguration, vararg tokenClaims: TokenClaims): String {
        var jsonWebServiceToken = JWT
            .create()
            .withAudience(tokenConfiguration.targetedAudience)
            .withIssuer(tokenConfiguration.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + tokenConfiguration.tokenExpiresIn))
        tokenClaims.forEach { claim ->
            jsonWebServiceToken = jsonWebServiceToken.withClaim(claim.name, claim.value)
        }
        return jsonWebServiceToken.sign(Algorithm.HMAC256(tokenConfiguration.tokenSecret))
    }
}