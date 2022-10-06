package com.note_application.plugins

import io.ktor.server.auth.*
import io.ktor.util.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.note_application.security.tokengeneration.TokenConfiguration
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureSecurity(tokenConfiguration: TokenConfiguration) {
    
    authentication {
            jwt {

                realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
                verifier(
                    JWT
                        .require(Algorithm.HMAC256(tokenConfiguration.tokenSecret))
                        .withAudience(tokenConfiguration.targetedAudience)
                        .withIssuer(tokenConfiguration.issuer)
                        .build()
                )
                validate { credential ->
                    if (credential.payload.audience.contains(tokenConfiguration.targetedAudience)) {
                        JWTPrincipal(credential.payload)
                    } else null
                }
            }
    }

}
