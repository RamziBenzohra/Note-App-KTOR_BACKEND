package com.note_application.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.awt.Color

data class Note (
    val posterId: ObjectId ,
    val title:String,
    val subTitle:String,
    val noteText:String,
    val color: String,
    val imageLink:String?,
    val webLink:String?,
    val timeStamp:Long,
    @BsonId
    val id:ObjectId = ObjectId()
        )