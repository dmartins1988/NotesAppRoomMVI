package com.example.notesroommvi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesroommvi.domain.model.Note
import com.example.notesroommvi.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NoteState())
    val state: StateFlow<NoteState> = _state.asStateFlow()

    fun dispatchIntent(intent: NoteIntent) {
        when (intent) {
            NoteIntent.DeleteNotes -> deleteNotes()
            NoteIntent.GetNotes -> getAllNotes()
            is NoteIntent.InsertNote -> insertNote(intent.note)
        }
    }

    private fun insertNote(note: Note) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                repository.insertNote(note)
                getAllNotes()
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error on insert note on database"
                    )
                }
            }
        }
    }

    private fun getAllNotes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                repository.getNotes().collect { notes ->
                    _state.update { it.copy(isLoading = false, notes = notes) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error on get notes from database"
                    )
                }
            }
        }
    }

    private fun deleteNotes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                repository.deleteAllNotes()
                getAllNotes()
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error on deleting from database"
                    )
                }
            }
        }
    }
}