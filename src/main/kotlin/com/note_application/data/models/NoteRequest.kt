package com.note_application.data.models

import kotlinx.serialization.Serializable



@Serializable
data class NoteRequest (
    val posterId: String,
    val title:String="",
    val subTitle:String="",
    val noteText:String="",
    val color: String="",
    val imageLink:String="",
    val webLink:String="",
    val timeStamp:Long,
    val id: String )