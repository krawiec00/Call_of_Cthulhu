package com.app.callofcthulhu.model.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class TokenRepository {


    fun getToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val deviceToken = task.result
                // Wywołaj funkcję do zapisania tokena w Firestore
                saveDeviceTokenToFirestore(deviceToken)
            }
        }
    }

    private fun saveDeviceTokenToFirestore(deviceToken: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val userDocRef = FirebaseFirestore.getInstance().collection("user_logs").document(userId)

            // Aktualizuj lub utwórz dokument z tokenem urządzenia
            userDocRef.update("Token", deviceToken)
                .addOnSuccessListener {
                    Log.d("TAG", "Device token  updated successfully")
                }
                .addOnFailureListener {
                    Log.e("TAG", "Error updating device token", it)
                }
        }
    }

}