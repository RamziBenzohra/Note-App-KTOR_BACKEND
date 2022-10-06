package com.note_application.data.db

import com.note_application.data.models.Note
import com.note_application.data.models.User
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.toId

class MongodbOperationImpl (db:CoroutineDatabase) : MongodbOperation {

    private val usersCollection = db.getCollection<User>()
    private val notesCollection = db.getCollection<Note>()

    override suspend fun insertUser(user: User): Boolean {
        return usersCollection.insertOne(user).wasAcknowledged()
    }

    override suspend fun getUserInfoByUsername(username: String): User? {
        return try {
            usersCollection.findOne(User::username eq username)
        }catch (e:Exception){
            e.printStackTrace()
            null
        }

    }

    override suspend fun getUserInfoById(id: String): User? {
        return usersCollection.findOneById(ObjectId(id))
    }

    override suspend fun insertNote(note: Note): Boolean {
        return notesCollection.insertOne(note).wasAcknowledged()
    }

    override suspend fun getAllNotes(userId:String): List<Note> {
        return notesCollection.find( Note::posterId eq ObjectId(userId)).descendingSort(Note::timeStamp).toList()

    }

    override suspend fun updateNote(id: String,note: Note): Note? {
        return id.let {
            val isUpdated = notesCollection.updateOneById(ObjectId(id),note).wasAcknowledged()
            if (isUpdated) {
                getNoteByNoteId(it)
            }else{
                null
            }
        }
    }


    override suspend fun deleteAllNote(): Boolean {
        return notesCollection.deleteMany().wasAcknowledged()
    }

    override suspend fun getNoteByNoteId(id: String): Note? {
        return notesCollection.findOneById(ObjectId(id))
    }

    override suspend fun deleteNote(id: String,poster: String): List<Note>? {
        return if (notesCollection.deleteOneById(ObjectId(id)).wasAcknowledged()){
            getAllNotes(poster)
        }else{
            return null
        }
    }
}