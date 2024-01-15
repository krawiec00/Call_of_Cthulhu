package com.app.callofcthulhu.view.authorization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.app.callofcthulhu.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


class PasswordResetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)


        val editTextEmail = findViewById<TextInputEditText>(R.id.resetEmail)
        val buttonReset = findViewById<Button>(R.id.btn_reset)


        buttonReset.setOnClickListener{
            val auth = FirebaseAuth.getInstance()
            val email = editTextEmail.text.toString()
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Wysłano maila z resetem hasła", Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(baseContext, "Błąd z wysłaniem maila", Toast.LENGTH_SHORT).show()
                    }
                }
        }


    }
}