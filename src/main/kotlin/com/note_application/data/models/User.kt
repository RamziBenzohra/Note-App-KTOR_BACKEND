package com.note_application.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    val username:String,
    val password:String,
    val saltForPassword: String,
    @BsonId val id:ObjectId = ObjectId()
)

