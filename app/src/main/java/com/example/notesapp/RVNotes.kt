package com.example.notesapp

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_row.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RVNotes(private var notes: ArrayList<Note>, private val context: Context):
    RecyclerView.Adapter<RVNotes.ItemViewHolder>() {
    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view)

    private val noteDatabase by lazy { NoteDatabase.getInstance(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val note = notes[position]

        holder.itemView.apply {
            tvNote.text = note.text

            btnUpdate.setOnClickListener { updateNote(note) }
            btnDelete.setOnClickListener { deleteNote(note) }
        }
    }

    override fun getItemCount() = notes.size

    fun update(){
        CoroutineScope(Dispatchers.IO).launch {
            notes = noteDatabase.getNoteDao().getAllNotes() as ArrayList<Note>
            withContext(Dispatchers.Main){
                notifyDataSetChanged()
            }
        }
    }

    private fun updateNote(note: Note){
        val et = EditText(context)
        et.setText(note.text)
        AlertDialog.Builder(context)
            .setTitle("Update Note")
            .setView(et)
            .setPositiveButton("Edit"){_,_ ->
                CoroutineScope(Dispatchers.IO).launch {
                    note.text = et.text.toString()
                    noteDatabase.getNoteDao().updateNote(note)
                    withContext(Dispatchers.Main){ update() }
                }
            }
            .setNegativeButton("Cancel"){face,_ -> face.cancel()}
            .create()
            .show()
    }

    private fun deleteNote(note: Note){
        AlertDialog.Builder(context)
            .setTitle("Delete Note")
            .setMessage("Do You Want To Delete This Note?")
            .setPositiveButton("Yes"){_,_ ->
                CoroutineScope(Dispatchers.IO).launch {
                    noteDatabase.getNoteDao().deleteNote(note)
                    withContext(Dispatchers.Main){ update() }
                }
            }
            .setNegativeButton("No"){face,_ -> face.cancel()}
            .create()
            .show()
    }
}