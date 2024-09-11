package com.example.notesroommvi.data.repository

import com.example.notesroommvi.data.database.NoteDao
import com.example.notesroommvi.di.IODispatcher
import com.example.notesroommvi.domain.mapper.toNote
import com.example.notesroommvi.domain.mapper.toNoteEntity
import com.example.notesroommvi.domain.model.Note
import com.example.notesroommvi.domain.repository.NoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val dao: NoteDao,
    @IODispatcher private val ioScope: CoroutineDispatcher
) : NoteRepository {
    override fun getNotes(): Flow<List<Note>> {
        return dao.getAllNotes().map { it.map { noteEntity -> noteEntity.toNote() } }
    }

    override suspend fun insertNote(note: Note) {
        withContext(ioScope) {
            dao.addNote(note.toNoteEntity())
        }
    }

    override suspend fun deleteAllNotes() {
        withContext(ioScope) {
            dao.deleteAllNotes()
        }
    }
}