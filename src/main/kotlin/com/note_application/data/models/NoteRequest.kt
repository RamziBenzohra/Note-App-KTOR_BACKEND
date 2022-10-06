package com.note_application.data.models

import kotlinx.serialization.Serializable


@Serializable
data class NoteRequest (
    val userId :String,
    val noteId:String
        ){
}