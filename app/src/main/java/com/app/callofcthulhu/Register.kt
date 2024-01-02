package com.app.callofcthulhu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class Register : AppCompatActivity() {


    public override fun onStart() {
        super.onStart()
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            val intent = Intent(applicationContext, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val editTextEmail = findViewById<TextInputEditText>(R.id.registerEmail)
        val editTextPassword = findViewById<TextInputEditText>(R.id.registerPassword)
        val buttonReg = findViewById<Button>(R.id.btn_register)
        auth = Firebase.auth
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val textView = findViewById<TextView>(R.id.loginNow)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        textView.setOnClickListener() {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()

        }

        buttonReg.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter both email and password", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Rejestracja powiodła się
                            val user = auth.currentUser
                            user?.sendEmailVerification()
                                ?.addOnCompleteListener { emailTask ->
                                    if (emailTask.isSuccessful) {
                                        Toast.makeText(
                                            baseContext,
                                            "Email verification sent to ${user.email}",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        val db = Firebase.firestore
                                        val userData = hashMapOf(
                                            "userId" to user.uid,
                                            "email" to user.email,
                                            "role" to "user"
                                        )

                                        // Dodawanie dokumentu użytkownika
                                        db.collection("user_logs").document(user.uid)
                                            .set(userData)
                                            .addOnSuccessListener {
                                                // Dodawanie aktywności rejestracji do podkolekcji 'actions'
                                                val logData = hashMapOf(
                                                    "actionName" to "Rejestracja",
                                                    "timestamp" to FieldValue.serverTimestamp()
                                                )

                                                db.collection("user_logs").document(user.uid)
                                                    .collection("actions").add(logData)
                                            }

                                        val bundle = Bundle()
                                        bundle.putString(FirebaseAnalytics.Param.METHOD, "email_password")
                                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)

                                    } else {
                                        // Obsługa błędu podczas wysyłania maila z potwierdzeniem
                                        Toast.makeText(
                                            baseContext,
                                            "Failed to send verification email.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            progressBar.visibility = View.GONE
                            // Przejdź do ekranu logowania po rejestracji
                            val intent = Intent(this, Login::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Rejestracja nie powiodła się
                            val error = task.exception?.message ?: "Authentication failed."
                            Toast.makeText(baseContext, error, Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                        }
                    }
            }
        }


    }
}