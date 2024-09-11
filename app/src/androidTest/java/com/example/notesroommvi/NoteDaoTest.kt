package com.example.notesroommvi

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.notesroommvi.data.database.NoteDao
import com.example.notesroommvi.data.database.NoteDatabase
import com.example.notesroommvi.data.database.NoteEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class NoteDaoTest {

    private lateinit var database: NoteDatabase
    private lateinit var dao: NoteDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NoteDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.noteDao()
    }

    @Test
    fun getAllNotesFromDatabase() = runTest {
        val note1 = NoteEntity(
            id = 1,
            title = "Title 1",
            description = "Description 1"
        )

        dao.addNote(note1)

        val allNotes = dao.getAllNotes().first()

        assertEquals(allNotes[0], note1)
    }

    @Test
    fun deleteAllNotesFromDatabase() = runTest {
        val listOfNotes = listOf(
            NoteEntity(
                id = 1,
                title = "Title 1",
                description = "Description 1"
            ),
            NoteEntity(
                id = 2,
                title = "Title 2",
                description = "Description 2"
            ),
            NoteEntity(
                id = 3,
                title = "Title 3",
                description = "Description 3"
            )
        )

        listOfNotes.forEach {  noteEntity ->
            dao.addNote(noteEntity)
        }

        dao.deleteAllNotes()

        val notes = dao.getAllNotes().first()

        assertTrue(notes.isEmpty())
    }

}