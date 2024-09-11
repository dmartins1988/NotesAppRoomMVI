package com.example.notesroommvi.domain.repository

import com.example.notesroommvi.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(): Flow<List<Note>>
    suspend fun insertNote(note: Note)
    suspend fun deleteAllNotes()
}