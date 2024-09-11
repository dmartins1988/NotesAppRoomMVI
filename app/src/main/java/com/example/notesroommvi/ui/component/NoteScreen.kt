package com.example.notesroommvi.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notesroommvi.domain.model.Note
import com.example.notesroommvi.presentation.NoteIntent
import com.example.notesroommvi.presentation.NoteViewModel

@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    var title by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    Column(modifier = modifier.padding(16.dp)) {
        TextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") })
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            viewModel.dispatchIntent(
                NoteIntent.InsertNote(
                    Note(
                        title = title,
                        description = description
                    )
                )
            )
            title = ""
            description = ""
        }) {
            Text("Add Note")
        }
        Button(onClick = {
            viewModel.dispatchIntent(NoteIntent.DeleteNotes)
        }) {
            Text(text = "Delete All Notes")
        }
        Spacer(modifier = Modifier.height(16.dp))


        if (state.value.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        if (state.value.notes.isNotEmpty()) {
            state.value.notes.forEach { note ->
                Text("Title: ${note.title}, Description: ${note.description}")
            }
        }

        state.value.errorMessage?.let { error -> Text(text = error) }

    }
}