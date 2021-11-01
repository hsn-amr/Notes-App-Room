package com.example.notesapp

import androidx.room.*

@Dao
interface NoteDao {

    @Query("SELECT * FROM text")
    fun getAllNotes(): List<Note>

    @Insert
    fun addNote(note: Note)

    @Update
    fun updateNote(note: Note)

    @Delete
    fun deleteNote(note: Note)
}