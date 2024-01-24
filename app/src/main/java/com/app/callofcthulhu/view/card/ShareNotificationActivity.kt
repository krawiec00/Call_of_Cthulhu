package com.app.callofcthulhu.view.card

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.callofcthulhu.R
import com.app.callofcthulhu.model.data.Card
import com.app.callofcthulhu.model.data.Request
import com.app.callofcthulhu.utils.Utility
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ShareNotificationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RequestAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_notification)

        recyclerView = findViewById(R.id.recycler_view)

        setupRecyclerView()

    }

    private fun setupRecyclerView() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val query = currentUserId?.let { userId ->
            FirebaseFirestore.getInstance()
                .collection("requests")
                .whereEqualTo("toUserId", userId)
        } ?: throw IllegalStateException("Brak zalogowanego użytkownika")


        val options = FirestoreRecyclerOptions.Builder<Request>()
            .setQuery(query, Request::class.java)
            .build()

        adapter = RequestAdapter(options, this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

}
