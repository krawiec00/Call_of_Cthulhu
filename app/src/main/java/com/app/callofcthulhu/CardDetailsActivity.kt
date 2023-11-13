package com.app.callofcthulhu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.app.callofcthulhu.ui.theme.Utility
import com.google.firebase.firestore.DocumentReference
import io.grpc.Context

class CardDetailsActivity : AppCompatActivity() {


    private lateinit var title : EditText
    private lateinit var content : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

         title = findViewById<EditText>(R.id.notes_title_text)
         content = findViewById<EditText>(R.id.notes_content_text)
         val saveCardBtn = findViewById<ImageButton>(R.id.save_note_btn)

         saveCardBtn.setOnClickListener { saveCard() }

    }

    fun saveCard(){
        val noteTile = title.text.toString()
        val noteContent = content.text.toString()
        if(noteTile==null|| noteTile.isEmpty()){
            title.setError("Title is required")
            return;
        }
        val card = Card()
        card.title = noteTile
        card.content = noteContent
        saveCardToFireBase(card)
    }

    fun saveCardToFireBase(card: Card){
        val documentReference : DocumentReference = Utility.getCollectionReferenceForNotes().document()
        documentReference.set(card).addOnCompleteListener {taskId ->
            if(taskId.isSuccessful){
                Toast.makeText(baseContext, "Note added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            else{
                Toast.makeText(baseContext, "Failed while adding note", Toast.LENGTH_SHORT).show()
            }
        }

    }




}