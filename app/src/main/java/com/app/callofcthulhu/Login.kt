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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Login : AppCompatActivity() {


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editTextEmail = findViewById<TextInputEditText>(R.id.email);
        val editTextPassword = findViewById<TextInputEditText>(R.id.password);
        val buttonLogin = findViewById<Button>(R.id.btn_login);
        auth = Firebase.auth
        val progressBar = findViewById<ProgressBar>(R.id.progressBar);
        val textView = findViewById<TextView>(R.id.registerNow)

        textView.setOnClickListener() {
            val intent = Intent(this, Register::class.java);
            startActivity(intent);
            finish();

        }

        buttonLogin.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter both email and password", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Logowanie powiodło się
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Logowanie nie powiodło się
                            val error = task.exception?.message ?: "Authentication failed."
                            Toast.makeText(baseContext, error, Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                        }
                    }
            }
        }

    }
}