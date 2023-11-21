package com.app.callofcthulhu

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference


class CardDetailsActivity : AppCompatActivity() {


    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var saveCardBtn: ImageButton
    lateinit var pageTitleTextView: TextView
    private var title: String? = null
    var content: String? = null
     private var docId: String = ""
    private var isEdited: Boolean = false
    lateinit var deleteCardBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        pageTitleTextView = findViewById(R.id.page_title)
        titleEditText = findViewById(R.id.notes_title_text)
        contentEditText = findViewById(R.id.notes_content_text)
        saveCardBtn = findViewById(R.id.save_note_btn)
        deleteCardBtn = findViewById(R.id.delete_card_btn)

        //pobrane dane do wyświetlenia do edycji
        title = intent.getStringExtra("title")
        content = intent.getStringExtra("content")
        docId = intent.getStringExtra("docId") ?: ""

        if (docId.isNotEmpty()) {
            isEdited = true
        }

        titleEditText.setText(title)
        contentEditText.setText(content)
        if (isEdited) {
            pageTitleTextView.setText("Edit note")
            deleteCardBtn.visibility = View.VISIBLE
        }


        saveCardBtn.setOnClickListener { saveCard() }

        deleteCardBtn.setOnClickListener{ showDeleteConfirmationDialog()}

    }

    fun saveCard() {
        val noteTile = titleEditText.text.toString()
        val noteContent = contentEditText.text.toString()
        if (noteTile == null || noteTile.isEmpty()) {
            titleEditText.setError("Title is required")
            return;
        }
        val card = Card()
        card.title = noteTile
        card.content = noteContent
        saveCardToFireBase(card)
    }

    fun saveCardToFireBase(card: Card) {
        val documentReference: DocumentReference = if (isEdited) {
            // Update the note
            Utility.getCollectionReferenceForNotes().document(docId)
        } else {
            // Create new note
            Utility.getCollectionReferenceForNotes().document()
        }

        documentReference.set(card).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(baseContext, "Note added successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(baseContext, "Failed while adding note", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteCardFromFirebase() {
        val documentReference: DocumentReference
        documentReference = Utility.getCollectionReferenceForNotes().document(docId)
        documentReference.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //note is deleted
                Toast.makeText(baseContext, "Card deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(baseContext, "Card failed to delete", Toast.LENGTH_SHORT).show()

            }
        }
    }

    fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Potwierdzenie")
        builder.setMessage("Czy na pewno chcesz usunąć tę kartę?")

        builder.setPositiveButton("Tak") { dialog, _ ->
            deleteCardFromFirebase() // Wywołaj funkcję usuwania po potwierdzeniu
            dialog.dismiss()
        }

        builder.setNegativeButton("Nie") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

    }
}