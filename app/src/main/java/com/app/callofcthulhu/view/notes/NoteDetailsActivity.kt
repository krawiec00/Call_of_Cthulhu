package com.app.callofcthulhu.view.notes

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.callofcthulhu.R
import com.app.callofcthulhu.utils.Utility
import com.app.callofcthulhu.view.card.CardDetailsActivity.Companion.docId
import com.app.callofcthulhu.model.data.Note
import com.google.firebase.firestore.DocumentReference


class NoteDetailsActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var saveNoteBtn: ImageButton
    private lateinit var deleteNoteBtn: ImageButton
    private lateinit var page_title: TextView
    private var content: String? = null
    private var title: String? = null
    var noteId: String = ""
    var isEdited: Boolean = false


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        titleEditText = findViewById(R.id.notes_title_text)
        contentEditText = findViewById(R.id.notes_content_text)
        saveNoteBtn = findViewById(R.id.save_note_btn)
        deleteNoteBtn = findViewById(R.id.delete_card_btn)
        page_title = findViewById(R.id.page_title)

        saveNoteBtn.setOnClickListener(){
            saveNote()
        }


        title = intent.getStringExtra("title")
        content = intent.getStringExtra("content")
        noteId = intent.getStringExtra("noteId") ?: ""

        titleEditText.setText(title)
        contentEditText.setText(content)
        if(noteId.isNotEmpty()){
            isEdited = true
        }

        if(isEdited){
            page_title.text = "Edytuj notatkę"
            deleteNoteBtn.visibility = View.VISIBLE

        }

        deleteNoteBtn.setOnClickListener(){
            showDeleteConfirmationDialog()
        }


    }

    private fun saveNote() {
        val noteTitle = titleEditText.text.toString()
        val noteContent = contentEditText.text.toString()

        if (noteTitle.isEmpty()) {
            titleEditText.error = "Title is required"
            return
        }

        val note = Note().apply {
            title = noteTitle
            content = noteContent
            id = docId
        }

        saveNoteToFireBase(note)
    }

    private fun saveNoteToFireBase(note: Note) {
        val documentReference: DocumentReference = if (noteId.isNotEmpty()) {
            // Update the note
            Utility.getCollectionReferenceForNotes().document(noteId)
        } else {
            // Create new note
            Utility.getCollectionReferenceForNotes().document()
        }

        documentReference.set(note).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(baseContext, "Notatka dodana", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(baseContext, "Usunięto notatkę", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteNoteFromFirebase() {
        val documentReference: DocumentReference = Utility.getCollectionReferenceForNotes().document(noteId)
        documentReference.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //note is deleted
                Toast.makeText(baseContext, "Note deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(baseContext, "Note failed to delete", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Potwierdzenie")
        builder.setMessage("Czy na pewno chcesz usunąć tę notatkę?")

        builder.setPositiveButton("Tak") { dialog, _ ->
            deleteNoteFromFirebase() // Wywołaj funkcję usuwania po potwierdzeniu
            dialog.dismiss()
        }

        builder.setNegativeButton("Nie") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

    }

}