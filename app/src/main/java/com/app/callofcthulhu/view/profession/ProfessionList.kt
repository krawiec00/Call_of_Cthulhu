package com.app.callofcthulhu.view.profession

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.app.callofcthulhu.R
import com.app.callofcthulhu.utils.SharedViewModelInstance
import com.google.firebase.firestore.FirebaseFirestore

class ProfessionList : AppCompatActivity() {
    var sharedViewModel = SharedViewModelInstance.instance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profession_list)


        val db = FirebaseFirestore.getInstance()
        db.collection("professions").get().addOnSuccessListener { result ->
            val documentIds = ArrayList<String>()
            for (document in result) {
                documentIds.add(document.id) // Dodawanie identyfikatora dokumentu do listy
            }
            // Wyszukujemy ListView tylko raz i ustawiamy adapter
            val listView = findViewById<ListView>(R.id.documentsListView)
            val adapter = ArrayAdapter(this, R.layout.custom_list_item, R.id.item_name, documentIds)
            listView.adapter = adapter

            // Ustawienie słuchacza kliknięć, aby przechodzić do innej aktywności z identyfikatorem dokumentu
            listView.setOnItemClickListener { parent, view, position, id ->
                val documentId = parent.getItemAtPosition(position) as String
                val intent = Intent(this, ProfessionDetails::class.java).apply {
                    putExtra("documentId", documentId)
                }
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            // Obsłuż błąd
        }



    }
}