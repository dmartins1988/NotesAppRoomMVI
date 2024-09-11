package com.example.notesroommvi.presentation

import app.cash.turbine.test
import com.example.notesroommvi.domain.model.Note
import com.example.notesroommvi.domain.repository.NoteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModelTest {

    private lateinit var viewModel: NoteViewModel
    private lateinit var repository: NoteRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        viewModel = NoteViewModel(repository)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `dispatch Get Notes to get all the notes and updates state`() = runTest {
        // Given
        val notes = listOf(
            Note("Note 1", "Description 1"),
            Note("Note 2", "Description 2")
        )
        val notesFlow = flowOf(notes)
        coEvery { repository.getNotes() } returns notesFlow

        // When
        viewModel.dispatchIntent(NoteIntent.GetNotes)

        // Then
        viewModel.state.test {
            assertEquals(NoteState(isLoading = false), awaitItem()) // Initial value
            assertEquals(NoteState(isLoading = true), awaitItem())
            assertEquals(NoteState(isLoading = false, notes = notes), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { repository.getNotes() }
    }

    @Test
    fun `dispatch Delete Notes to delete all notes and updated state`() = runTest {
        // Given
        coEvery { repository.deleteAllNotes() } returns Unit
        coEvery { repository.getNotes() } returns flowOf(emptyList())

        // When
        viewModel.dispatchIntent(NoteIntent.DeleteNotes)

        // Then
        viewModel.state.test {
            assertEquals(NoteState(isLoading = false), awaitItem()) // Initial value
            assertEquals(NoteState(isLoading = true), awaitItem())
            assertEquals(NoteState(isLoading = false, notes = emptyList()), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { repository.deleteAllNotes() }
        coVerify { repository.getNotes() }
    }

    @Test
    fun `dispatch Insert Note to insert note and update state`() = runTest {
        // Given
        val note = Note("Note 1", "Description 1")
        coEvery { repository.insertNote(note) } returns Unit
        coEvery { repository.getNotes() } returns flowOf(listOf(note))

        // When
        viewModel.dispatchIntent(NoteIntent.InsertNote(note))

        // Then
        viewModel.state.test {
            assertEquals(NoteState(isLoading = false), awaitItem())
            assertEquals(NoteState(isLoading = true), awaitItem())
            assertEquals(NoteState(isLoading = false, notes = listOf(note)), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { repository.insertNote(note) }
        coVerify { repository.getNotes() }
    }

    @Test
    fun `insert Note throws exception and update state with error`() = runTest {
        val note = Note("Note 1", "Description 1")
        coEvery { repository.insertNote(note) } throws Exception("Error")

        viewModel.dispatchIntent(NoteIntent.InsertNote(note))

        viewModel.state.test {
            assertEquals(NoteState(isLoading = false), awaitItem())
            assertEquals(NoteState(isLoading = true), awaitItem())
            assertEquals(
                NoteState(isLoading = false, errorMessage = "Error on insert note on database"),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { repository.insertNote(note) }
    }

    @Test
    fun `get all Notes throws exception and update state with error`() = runTest {
        coEvery { repository.getNotes() } throws Exception("Error get all notes")

        viewModel.dispatchIntent(NoteIntent.GetNotes)

        viewModel.state.test {
            assertEquals(NoteState(isLoading = false), awaitItem())
            assertEquals(NoteState(isLoading = true), awaitItem())
            assertEquals(
                NoteState(isLoading = false, errorMessage = "Error on get notes from database"),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { repository.getNotes() }
    }

    @Test
    fun `delete Notes throws exception and update state with error`() = runTest {
        coEvery { repository.deleteAllNotes() } throws Exception("Error get all notes")

        viewModel.dispatchIntent(NoteIntent.DeleteNotes)

        viewModel.state.test {
            assertEquals(NoteState(isLoading = false), awaitItem())
            assertEquals(NoteState(isLoading = true), awaitItem())
            assertEquals(
                NoteState(isLoading = false, errorMessage = "Error on deleting from database"),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { repository.deleteAllNotes() }
    }
}