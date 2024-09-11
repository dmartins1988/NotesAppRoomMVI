package com.example.notesroommvi.domain.mapper

import com.example.notesroommvi.data.database.NoteEntity
import com.example.notesroommvi.domain.model.Note

fun NoteEntity.toNote(): Note {
    return Note(
        title = title,
        description = description
    )
}

fun Note.toNoteEntity(): NoteEntity {
    return NoteEntity(
        title = title,
        description = description
    )
}