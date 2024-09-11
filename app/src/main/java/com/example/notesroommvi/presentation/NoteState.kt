package com.example.notesroommvi.presentation

import com.example.notesroommvi.domain.model.Note

data class NoteState(
    val isLoading: Boolean = false,
    val notes: List<Note> = emptyList(),
    val errorMessage: String? = null
)
