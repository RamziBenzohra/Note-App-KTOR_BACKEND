package com.note_application.data.db

import com.note_application.data.models.Note
import com.note_application.data.models.User


interface MongodbOperation {
    suspend fun  insertUser(user: User):Boolean
    suspend fun  getUserInfoByUsername(username:String):User?
    suspend fun  getUserInfoById(id:String):User?
    suspend fun  insertNote(note:Note):Boolean
    suspend fun  getAllNotes(userId:String):List<Note>
    suspend fun  updateNote(id: String,note: Note):Note?
    suspend fun  getNoteByNoteId(id:String):Note?
    suspend fun  deleteNote(id: String,poster: String):List<Note>?
    suspend fun  deleteAllNote():Boolean
}