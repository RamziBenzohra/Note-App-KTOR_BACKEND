package com.note_application.plugins

import com.note_application.data.db.MongodbOperation
import com.note_application.routes.authentication.authenticate
import com.note_application.routes.authentication.logInUser
import com.note_application.routes.authentication.registerNewUser
import com.note_application.routes.notes.notesRoute
import com.note_application.security.passwordHashing.Hashing
import com.note_application.security.tokengeneration.TokenConfiguration
import com.note_application.security.tokengeneration.TokenGeneration
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting( mongodbOperation: MongodbOperation,
                                  hashing: Hashing,
                                  tokenGeneration: TokenGeneration,
                                  tokenConfiguration: TokenConfiguration) {
    routing {
        registerNewUser(mongodbOperation, hashing)
        logInUser(mongodbOperation, hashing, tokenGeneration, tokenConfiguration)
        authenticate()

    }
    routing (){
        route("notes") {
            notesRoute(mongodbOperation)
        }
    }


}
