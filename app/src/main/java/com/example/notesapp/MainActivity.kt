package com.example.notesapp

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val noteDatabase by lazy { NoteDatabase.getInstance(applicationContext) }
    lateinit var adapter: RVNotes
    lateinit var rvMain: RecyclerView
    lateinit var noteInput: EditText
    lateinit var addNote: Button

    var allNotes = ArrayList<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvMain = findViewById(R.id.rvMain)
        noteInput = findViewById(R.id.etNoteInput)
        addNote = findViewById(R.id.btnAddNote)

        CoroutineScope(Dispatchers.IO).launch {
            allNotes = noteDatabase.getNoteDao().getAllNotes() as ArrayList<Note>
            withContext(Dispatchers.Main){
                adapter = RVNotes(allNotes,this@MainActivity)
                noteInput.text.clear()
                rvMain.adapter = adapter
                rvMain.layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }

        addNote.setOnClickListener {
            val noteText = noteInput.text.toString()
            if(noteText.isNotEmpty()){
                CoroutineScope(Dispatchers.IO).launch {
                    noteDatabase.getNoteDao().addNote(Note(0,noteText))
                    withContext(Dispatchers.Main){
                        adapter.update()
                    }
                }
            }else{
                Toast.makeText(this, "Please, Type a note", Toast.LENGTH_LONG).show()
            }
        }
    }

}