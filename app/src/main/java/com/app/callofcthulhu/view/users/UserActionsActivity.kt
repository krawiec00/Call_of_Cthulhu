package com.app.callofcthulhu.view.users

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.app.callofcthulhu.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale


class UserActionsActivity : AppCompatActivity() {


    private var listenerRegistration: ListenerRegistration? = null

    private lateinit var userMailTextView: TextView
    private lateinit var buttonUser: Button
    private lateinit var checkbox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_actions)

        userMailTextView = findViewById(R.id.userMail)
        val buttonLog = findViewById<Button>(R.id.btn_log_delete)
        buttonUser = findViewById(R.id.btn_user_delete)
        checkbox = findViewById(R.id.checkbox_id)

        // Pobranie danych przekazanych z poprzedniej aktywności
        val userId = intent.getStringExtra("userId") ?: ""
        val userMail = intent.getStringExtra("userEmail") ?: ""

        userMailTextView.text = userMail

        fetchUserData(userId)



        buttonLog.setOnClickListener() {
            deleteAllUserLogs(userId)
        }

        buttonUser.setOnClickListener() {
            deleteWholeLog(userId)

        }


        val userLogRef = FirebaseFirestore.getInstance().collection("user_logs").document(userId)

        checkbox.setOnCheckedChangeListener { _, isChecked ->
            val newRole = if (isChecked) "admin" else "user"

            // Aktualizacja pola "role" w dokumencie użytkownika
            userLogRef.update("role", newRole)
        }

        userLogRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val role = documentSnapshot.getString("role")
                    role?.let {
                        checkbox.isChecked = it == "admin"
                    }
                }
            }

    }

    private fun formatTimestamp(timestamp: Timestamp): String {
        val date = timestamp.toDate()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(date)
    }

    // Pobieranie akcji z Firestore z datą i godziną
    override fun onStart() {
        super.onStart()
        val userMail = intent.getStringExtra("userEmail") ?: ""
        val userId = intent.getStringExtra("userId") ?: ""
        userMailTextView.text = userMail
        getActionsForUser(userId)
    }

    override fun onStop() {
        super.onStop()
        listenerRegistration?.remove()
        Log.e("TEST", " KOŃCZYMY NASŁUCH")
    }

    private fun getActionsForUser(userId: String) {
        val db = Firebase.firestore
        val actionsCollection = db.collection("user_logs").document(userId).collection("actions")

        listenerRegistration = actionsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    // Obsłuż błąd
                    return@addSnapshotListener
                }

                val actionsList = mutableListOf<String>()

                for (document in snapshots!!) {
                    val actionName = document.getString("actionName") ?: ""
                    val timestamp = document.getTimestamp("timestamp")

                    if (timestamp != null) {
                        val formattedTimestamp = formatTimestamp(timestamp)
                        val actionWithTimestamp = "$actionName - $formattedTimestamp"
                        actionsList.add(actionWithTimestamp)
                    }
                }

                displayActions(actionsList)
            }
    }

    private fun displayActions(actionsList: List<String>) {
        val linearLayout = findViewById<LinearLayout>(R.id.containerLayout)

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 0, 20)

        for (action in actionsList) {
            var actionExists = false

            // Sprawdzenie, czy widok już istnieje w kontenerze
            for (i in 0 until linearLayout.childCount) {
                val childView = linearLayout.getChildAt(i)
                if (childView is TextView && childView.text == action) {
                    actionExists = true
                    break
                }
            }

            // Jeśli widoku jeszcze nie ma w kontenerze, dodaj go
            if (!actionExists) {
                val textView = TextView(this)
                textView.text = action
                textView.textSize = 18f
                textView.layoutParams = layoutParams

                linearLayout.addView(textView)
            }
        }
    }


    private fun deleteAllUserLogs(userId: String) {
        val db = Firebase.firestore
        val actionsCollection = db.collection("user_logs").document(userId).collection("actions")

        actionsCollection.get()
            .addOnSuccessListener { documents ->
                val batch = db.batch()

                for (document in documents) {
                    val docRef = actionsCollection.document(document.id)
                    batch.delete(docRef)
                }

                batch.commit()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Usunięto logi użytkownika", Toast.LENGTH_SHORT).show()
                        recreate()
                    }
                    .addOnFailureListener { exception ->
                        // Obsłuż błąd podczas usuwania dokumentów z podkolekcji
                    }
            }
            .addOnFailureListener { exception ->
                // Obsłuż błąd podczas pobierania dokumentów z podkolekcji
            }
    }

    private fun deleteWholeLog(userId: String) {
        deleteAllUserLogs(userId)
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("user_logs")
        val docRef = collectionRef.document(userId)

        docRef.delete()
            .addOnSuccessListener {
                // Usunięcie zakończone sukcesem
                Toast.makeText(this, "Usunięto profil z logami użytkownika", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Problem z usuwaniem profilu", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchUserData(userId: String) {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("user_logs").document(userId).collection("actions")

        collectionRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val actionName = document.getString("actionName")
                        if (actionName != null && actionName == "Usunięto konto") {
                            buttonUser.visibility = View.VISIBLE
                            checkbox.visibility = View.GONE
                            break
                        }
                    }
                }
            }
    }


}