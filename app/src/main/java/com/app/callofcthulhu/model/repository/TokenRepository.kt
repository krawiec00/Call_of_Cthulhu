package com.app.callofcthulhu.model.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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

            // Dodaj nowy token do listy tokenów urządzeń
            userDocRef.update("tokens", FieldValue.arrayUnion(deviceToken))
                .addOnSuccessListener {
                    Log.d("TAG", "Device token added successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("TAG", "Error adding device token", e)
                }
        }
    }


    fun removeToken(callback: () -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val userDocRef = FirebaseFirestore.getInstance().collection("user_logs").document(userId)

            // Pobierz aktualny token urządzenia
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val deviceToken = task.result

                    // Usuń token z listy tokens w bazie danych
                    userDocRef.update("tokens", FieldValue.arrayRemove(deviceToken))
                        .addOnSuccessListener {
                            Log.d("TAG", "Device token removed successfully")
                            callback() // Wywołaj funkcję zwrotną po zakończeniu operacji usuwania tokenu
                        }
                        .addOnFailureListener { e ->
                            Log.e("TAG", "Error removing device token", e)
                        }
                } else {
                    Log.e("TAG", "Error getting device token", task.exception)
                }
            }
        }
    }



}