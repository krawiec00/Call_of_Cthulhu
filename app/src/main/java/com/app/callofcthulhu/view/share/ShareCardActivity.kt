package com.app.callofcthulhu.view.share

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.app.callofcthulhu.services.RetrofitClient
import com.app.callofcthulhu.databinding.ActivityShareCardBinding
import com.app.callofcthulhu.model.data.Notification
import com.app.callofcthulhu.model.data.Request
import com.app.callofcthulhu.view.card.CardDetailsActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShareCardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShareCardBinding
    var docId = CardDetailsActivity.docId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShareCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Dostęp do widoków za pomocą obiektu wiążącego
        binding.emailShareText
        binding.btnShare
        binding.btnBack

        // Ustawienie nasłuchiwacza kliknięć
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnShare.setOnClickListener {
            val email = binding.emailShareText.text.toString()
            if (email.isNotEmpty()) {
                val db = Firebase.firestore
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userEmail = currentUser?.email
                db.collection("user_logs")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            binding.emailShareText.error = "Nie znaleziono użytkownika"
                        } else {
                            for (document in documents) {
                                val userId = document.id
                                val deviceTokens = document.get("tokens") as? List<String>
                                if (!deviceTokens.isNullOrEmpty()) {
                                    sendNotificationToServer(
                                        deviceTokens,
                                        "Udostępniono kartę",
                                        "Użytkownik $userEmail udostępnił ci swoją kartę postaci"
                                    )
                                    val nazwa = intent.getStringExtra("nazwa").toString()
                                    val timestamp = intent.getStringExtra("timestamp")
                                    val request = Request(
                                        currentUser?.uid.toString(),
                                        userEmail,
                                        userId,
                                        docId,
                                        "Pending",
                                        nazwa,
                                        timestamp
                                    )
                                    saveRequestToFirebase(request)
                                }
                            }

                        }


                    }

            }

        }
    }

    private fun saveRequestToFirebase(request: Request) {
        val documentReference = FirebaseFirestore.getInstance().collection("requests").document()
        documentReference.set(request).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(baseContext, "Udostępniono kartę", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(baseContext, "Blad przy udostępnianiu", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun sendNotificationToServer(tokens: List<String>, title: String, body: String) {
        for (token in tokens) {
            val notificationData = Notification(token, title, body)
            RetrofitClient.apiService.sendNotification(notificationData)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            Log.e("TAG", "WYSŁANO")
                        } else {
                            Log.e("TAG", "BŁĄD")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("API Error", "Error sending API request", t)
                    }
                })
        }
    }


}