package com.example.notesroommvi.presentation

import com.example.notesroommvi.domain.model.Note

sealed class NoteIntent {
    data object DeleteNotes : NoteIntent()
    data object GetNotes : NoteIntent()
    data class InsertNote(val note: Note) : NoteIntent()
}
