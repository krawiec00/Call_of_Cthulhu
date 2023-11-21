package com.app.callofcthulhu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query

class MainActivity : ComponentActivity() {


    private lateinit var auth: FirebaseAuth
    lateinit var recyclerView: RecyclerView
    lateinit var button: Button
    lateinit var cardAdapter: CardAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        button = findViewById(R.id.logout)
        val textView = findViewById<TextView>(R.id.user_details)
        val user = Firebase.auth.currentUser
        val cardButton = findViewById<FloatingActionButton>(R.id.add_note_btn)

        recyclerView = findViewById(R.id.recycler_view)

        if (user == null) {
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        } else {
            textView.text = user.email
        }

        //guzik logowania
        button.setOnClickListener() {
            if (auth.currentUser != null) {
                Firebase.auth.signOut()
                val intent = Intent(applicationContext, Login::class.java)
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(baseContext, "niezalogowany", Toast.LENGTH_SHORT).show()
            }
        }
        //wyświetlanie listy kart
        cardButton.setOnClickListener() {
            startActivity(Intent(this@MainActivity, CardDetailsActivity::class.java))
        }
        setupRecyclerView()

    }

    fun setupRecyclerView() {
        val query: Query =
            Utility.getCollectionReferenceForNotes().orderBy("title", Query.Direction.DESCENDING)
        val options: FirestoreRecyclerOptions<Card> = FirestoreRecyclerOptions.Builder<Card>()
            .setQuery(query, Card::class.java)
            .build()
        recyclerView.layoutManager = LinearLayoutManager(this)
        cardAdapter = CardAdapter(options, this)
        recyclerView.adapter = cardAdapter
    }

    override fun onStart() {
        super.onStart()
        cardAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        cardAdapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
        cardAdapter.notifyDataSetChanged()
    }

}

