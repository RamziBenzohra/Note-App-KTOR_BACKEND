package com.note_application.routes.authentication

import com.note_application.data.db.MongodbOperation
import com.note_application.data.models.AuthenticationRequest
import com.note_application.data.models.AuthenticationResponse
import com.note_application.data.models.User
import com.note_application.security.passwordHashing.Hashing
import com.note_application.security.passwordHashing.SaltAndPasswordHashed
import com.note_application.security.tokengeneration.TokenClaims
import com.note_application.security.tokengeneration.TokenConfiguration
import com.note_application.security.tokengeneration.TokenGeneration
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerNewUser(
    mongodbOperation: MongodbOperation,
    hashing: Hashing
) {
    post ("register") {
        val authenticationRequest = call.receiveNullable<AuthenticationRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest,"Empty Credential")
            return@post
        }
        val areCredentialsBlank = authenticationRequest.username.isBlank() || authenticationRequest.password.isBlank()
        val isPasswordShort = authenticationRequest.password.length < 8
        if(areCredentialsBlank) {
            call.respond(HttpStatusCode.Conflict,message = "Empty Password Or Username")
            return@post
        }else if (isPasswordShort){
            call.respond(HttpStatusCode.Conflict,message = "Password too short")
            return@post
        }
        val hashWithSalt = hashing.buildSaltedHash(authenticationRequest.password)
        val newUser =User(
            authenticationRequest.username,
            password = hashWithSalt.hash,
            saltForPassword = hashWithSalt.salt
        )
        val isOperationCompleted=mongodbOperation.insertUser(newUser)
        if (!isOperationCompleted){
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        call.respond(HttpStatusCode.OK,message = "Successfully registered : your id ${newUser.id}")
    }
}

fun Route.logInUser(
    mongodbOperation: MongodbOperation,
    hashing: Hashing,
    tokenGeneration: TokenGeneration,
    tokenConfiguration: TokenConfiguration
){
    post ("login") {
        val loginRequest = call.receiveNullable<AuthenticationRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest,"Empty Credential For Login")
            return@post
        }
        val requestedUser =mongodbOperation.getUserInfoByUsername(loginRequest.username)?:return@post call.respond(HttpStatusCode.BadRequest, "User does not exist")
        val isPasswordEnterCorrect = hashing.verifySaltedHash(
            passwordFromUserInput = loginRequest.password,
            saltAndHash = SaltAndPasswordHashed(
                hash = requestedUser.password,
                salt = requestedUser.saltForPassword
            )
        )
        if (!isPasswordEnterCorrect) return@post call.respond(HttpStatusCode.Conflict, "Incorrect password")
        val tokenGenerated = tokenGeneration.generateToken(
            tokenConfiguration = tokenConfiguration,
            TokenClaims(
                name = "userId",
                value = requestedUser.id.toString()
            )
        )
        call.respond(
            status = HttpStatusCode.OK,
            message = AuthenticationResponse(
                token = tokenGenerated,
                userId = requestedUser.id.toString()
            )
        )
    }
}

fun Route.authenticate() {
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}
/*
{
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJub3RlVXNlciIsImlzcyI6Imh0dHA6Ly8wLjAuMC4wOjgwODAiLCJleHAiOjE2OTU4NTEyNTMsInVzZXJJZCI6IjYzMzM2ZjUwZDNjMzA0NzUwZDFiNzI1OSJ9.RUcD79rb_aL1k4OpDmGbCvqBuzgGtl7v7tPDgb5gXz4",
    "userId": "63336f50d3c304750d1b7259"
}*/
