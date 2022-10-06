package com.note_application.routes.notes

import com.note_application.data.db.MongodbOperation
import com.note_application.data.extentionfunctions.save
import com.note_application.data.models.Note
import com.note_application.data.models.NoteRequest
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId


fun Route.notesRoute(mongodbOperation: MongodbOperation){
   authenticate {
       get(){
           val userId = call.request.queryParameters["userId"]?:return@get call.respondText (
               text = "Missing the User ID",
               status = HttpStatusCode.BadRequest
           )
           mongodbOperation.getUserInfoById(userId)?: return@get call.respondText (
               text = "User does not exist",
               status = HttpStatusCode.BadRequest
           )
           val notes = mongodbOperation.getAllNotes(userId)
           if (notes.isEmpty()) return@get call.respond(
               HttpStatusCode.Conflict,
               "There is no note registered in the database"
           )
           call.respond(
               HttpStatusCode.OK,
               notes
           )
       }
       delete() {
           val userId = call.request.queryParameters["userId"]?:return@delete call.respondText (
               text = "Missing the User ID",
               status = HttpStatusCode.BadRequest
           )
           mongodbOperation.getUserInfoById(userId)?: return@delete call.respondText (
               text = "User does not exist",
               status = HttpStatusCode.BadRequest
           )
           val deleteAll=mongodbOperation.deleteAllNote()
           if (!deleteAll){
                call.respondText (
                   text = "Failed to delete all",
                   status = HttpStatusCode.BadRequest
               )
               return@delete
           }
           call.respondText (
               text = "deleted all",
               status = HttpStatusCode.BadRequest
           )

       }
       get(path = "note") {
           val userId = call.request.queryParameters["userId"]?:return@get call.respondText (
               text = "Missing the User ID",
               status = HttpStatusCode.BadRequest
           )
           val noteId = call.request.queryParameters["noteId"]?:return@get call.respondText (
               text = "Missing the note ID",
               status = HttpStatusCode.BadRequest
           )
           mongodbOperation.getUserInfoById(userId)?: return@get call.respondText (
               text = "User does not exist",
               status = HttpStatusCode.BadRequest
           )
           val note = mongodbOperation.getNoteByNoteId(noteId)?:return@get call.respond(HttpStatusCode.Conflict,"Error : Note does not exist")
           call.respond(
               HttpStatusCode.OK,
               note
           )
       }
       post (path = "note") {
           val userId = call.request.queryParameters["userId"]?:return@post call.respondText (
               text = "Missing the User ID",
               status = HttpStatusCode.BadRequest
           )
           mongodbOperation.getUserInfoById(userId)?: return@post call.respondText (
               text = "User does not exist",
               status = HttpStatusCode.BadRequest
           )
           val title = call.request.queryParameters["title"]?:return@post call.respondText (
               text = "Missing the User ID",
               status = HttpStatusCode.BadRequest
           )
           val subTitle = call.request.queryParameters["subTitle"]?:""
           val noteText = call.request.queryParameters["noteText"]?:return@post call.respondText (
               text = "Missing the User ID",
               status = HttpStatusCode.BadRequest
           )
           val color = call.request.queryParameters["color"]?:"#000000"
           val imageLink = call.request.queryParameters["imageLink"]?:""
           val webLink = call.request.queryParameters["webLink"]?:""
           val newNote=Note(
               posterId = ObjectId(userId)
               ,title = title,
               subTitle = subTitle,
               noteText = noteText,
               color = color,
               imageLink = imageLink,
               webLink = webLink,
               timeStamp = System.currentTimeMillis()
           )
           val isInserted=mongodbOperation.insertNote(newNote)
           if (!isInserted){
               call.respondText (
                   text = "Error :Note note Inserted",
                   status = HttpStatusCode.Conflict
               )
               return@post
           }
           call.respondText (
               text = newNote.id.toString(),
               status = HttpStatusCode.OK
           )
       }
       delete (path = "note") {
           val userId = call.request.queryParameters["userId"]?:return@delete call.respondText (
               text = "Missing the User ID",
               status = HttpStatusCode.BadRequest
           )
           mongodbOperation.getUserInfoById(userId)?: return@delete call.respondText (
               text = "User does not exist",
               status = HttpStatusCode.BadRequest
           )
           val noteId = call.request.queryParameters["noteId"]?:return@delete call.respondText (
               text = "Missing the note ID",
               status = HttpStatusCode.BadRequest
           )
           val isNoteExist = mongodbOperation.getNoteByNoteId(noteId)!=null
           if (isNoteExist){
               val newNotes=mongodbOperation.deleteNote(noteId,userId)?:return@delete call.respondText (
                   text = "Error: can't delete note",
                   status = HttpStatusCode.BadRequest
               )
               call.respond(message = newNotes, status = HttpStatusCode.OK)

           }else{
               call.respondText (
                   text = "Note Does not exist",
                   status = HttpStatusCode.BadRequest
               )
           }


       }
       patch (path = "note") {
           val userId = call.request.queryParameters["userId"]?:return@patch call.respondText (
               text = "Missing the User ID",
               status = HttpStatusCode.BadRequest
           )
           mongodbOperation.getUserInfoById(userId)?: return@patch call.respondText (
               text = "User does not exist",
               status = HttpStatusCode.BadRequest
           )
           val noteId = call.request.queryParameters["noteId"]?:return@patch call.respondText (
               text = "Missing the note ID",
               status = HttpStatusCode.BadRequest
           )
           val isNoteExist = mongodbOperation.getNoteByNoteId(noteId)!=null
           if (!isNoteExist){
               call.respondText (
                   text = "Note not exist",
                   status = HttpStatusCode.BadRequest
               )
               return@patch
           }
           val title = call.request.queryParameters["title"]?:return@patch call.respondText (
               text = "Missing the title",
               status = HttpStatusCode.BadRequest
           )
           val subTitle = call.request.queryParameters["subTitle"]?:""
           val noteText = call.request.queryParameters["noteText"]?:return@patch call.respondText (
               text = "Missing the noteText",
               status = HttpStatusCode.BadRequest
           )
           val color = call.request.queryParameters["color"]?:"#000000"
           val imageLink = call.request.queryParameters["imageLink"]?:""
           val webLink = call.request.queryParameters["webLink"]?:""
           val newNote=Note(
               id = ObjectId(noteId),
               posterId = ObjectId(userId),
               title = title,
               subTitle = subTitle,
               noteText = noteText,
               color = color,
               imageLink = imageLink,
               webLink = webLink,
               timeStamp = System.currentTimeMillis()
           )

           val noteUpdate = mongodbOperation.updateNote(noteId,newNote)
           if (noteUpdate!=null){
               call.respondText (
                   text = noteUpdate.id.toString(),
                   status = HttpStatusCode.OK
               )
           }else{
               call.respondText (
                   text = "Error Failed to update",
                   status = HttpStatusCode.OK
               )
           }

       }
   }
}




