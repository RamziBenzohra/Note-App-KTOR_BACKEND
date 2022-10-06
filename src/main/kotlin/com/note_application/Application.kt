package com.note_application


import com.note_application.data.db.MongodbOperationImpl
import io.ktor.server.application.*
import com.note_application.plugins.*
import com.note_application.security.passwordHashing.HashingImpl
import com.note_application.security.tokengeneration.TokenConfiguration
import com.note_application.security.tokengeneration.TokenGenerationImpl
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val dbPassword =System.getenv("dbPassword")
    val dbName = "NoteApp"
    val db =KMongo.createClient("mongodb+srv://ramben:$dbPassword@notes.b9pmxox.mongodb.net/$dbName?retryWrites=true&w=majority")
        .coroutine.getDatabase(dbName)
    val mongodbOperation = MongodbOperationImpl(db)
    val tokenGeneration = TokenGenerationImpl()
    val tokenConfiguration = TokenConfiguration(
        issuer = environment.config.property("jwt.issuer").getString(),
        targetedAudience = environment.config.property("jwt.audience").getString(),
        tokenExpiresIn = 365L * 1000L * 60L * 60L * 24L,
        tokenSecret = System.getenv("json_web_token_secret")
    )
    val hashingService = HashingImpl()
    configureRouting(
        hashing = hashingService,
        mongodbOperation = mongodbOperation,
        tokenGeneration = tokenGeneration,
        tokenConfiguration = tokenConfiguration
    )
    configureSerialization()
    configureMonitoring()
    configureSecurity(tokenConfiguration)

}
