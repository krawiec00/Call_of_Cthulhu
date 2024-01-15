package com.app.callofcthulhu.view.users

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.callofcthulhu.R
import com.app.callofcthulhu.model.data.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UsersListActivity : AppCompatActivity() {
    private lateinit var usersAdapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_list)

        val recyclerView = findViewById<RecyclerView>(R.id.usersRecyclerView)

        usersAdapter = UsersAdapter(emptyList()) { user ->
            val intent = Intent(this, UserActionsActivity::class.java)
            intent.putExtra("userId", user.userId)
            intent.putExtra("userEmail", user.email)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = usersAdapter

        // Pobieranie danych i ustawienie nasłuchiwania na zmiany
        getUsersFromFirebaseAndListen()
    }

    private fun getUsersFromFirebaseAndListen() {
        val db = Firebase.firestore
        val usersCollection = db.collection("user_logs")

        usersCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Obsługa błędu pobierania danych lub nasłuchiwania zmian
                return@addSnapshotListener
            }

            val usersList = mutableListOf<User>()

            snapshot?.forEach { document ->
                val userId = document.id
                val userEmail = document.getString("email") ?: ""
                val user = User(userId, userEmail)
                usersList.add(user)
            }

            usersAdapter.updateUsersList(usersList)
        }
    }
}






