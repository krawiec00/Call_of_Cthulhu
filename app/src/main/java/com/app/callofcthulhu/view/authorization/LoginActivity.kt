package com.app.callofcthulhu.view.authorization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.app.callofcthulhu.view.MainActivity
import com.app.callofcthulhu.R
import com.app.callofcthulhu.model.repository.TokenRepository
import com.app.callofcthulhu.utils.Utility
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                // Użytkownik jest zalogowany i potwierdził swój adres email
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Użytkownik jest zalogowany, ale nie potwierdził swój adres email
                Toast.makeText(
                    baseContext,
                    "Please verify your email first.",
                    Toast.LENGTH_SHORT
                ).show()
                // Możesz tu dodać dodatkową obsługę, np. przekierowanie do ekranu wysyłającego ponownie maila z potwierdzeniem
            }
        }
    }

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editTextEmail = findViewById<TextInputEditText>(R.id.loginEmail)
        val editTextPassword = findViewById<TextInputEditText>(R.id.loginPassword)
        val buttonLogin = findViewById<Button>(R.id.btn_login)
        auth = Firebase.auth
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val textView = findViewById<TextView>(R.id.registerNow)
        val textResetPassword = findViewById<TextView>(R.id.passwordNow)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)






        textView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()

        }

        textResetPassword.setOnClickListener {
            val intent = Intent(this, PasswordResetActivity::class.java)
            startActivity(intent)
            finish()
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
                            val tokenRepo = TokenRepository()
                            tokenRepo.getToken()
                            val user = auth.currentUser
                            if (user != null && user.isEmailVerified) {
                                // Logowanie powiodło się i e-mail został zweryfikowany
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                finish()

                                Utility.writeLogToFirebase("Logowanie")


                                val bundle = Bundle()
                                bundle.putString(FirebaseAnalytics.Param.METHOD, "email_password")
                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                            } else {
                                // E-mail nie został zweryfikowany, uniemożliwienie logowania
                                Toast.makeText(
                                    baseContext,
                                    "Please verify your email before logging in.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressBar.visibility = View.GONE
                                auth.signOut() // Wylogowanie użytkownika, który nie zweryfikował e-maila
                            }
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