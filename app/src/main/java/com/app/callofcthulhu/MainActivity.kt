package com.app.callofcthulhu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.callofcthulhu.ui.theme.CallOfCthulhuTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {


    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance()
        val button = findViewById<Button>(R.id.logout)
        val textView = findViewById<TextView>(R.id.user_details)
         val user = Firebase.auth.currentUser

        if(user==null){
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }
        else{
            textView.text = user.email
        }

        button.setOnClickListener(){
            if (auth.currentUser != null) {
                Firebase.auth.signOut()
                val intent = Intent(applicationContext, Login::class.java)
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(baseContext, "niezalogowany", Toast.LENGTH_SHORT).show()
            }
        }


    }
}

