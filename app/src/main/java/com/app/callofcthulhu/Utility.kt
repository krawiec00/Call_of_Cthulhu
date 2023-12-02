package com.app.callofcthulhu

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date

class Utility {

    companion object {
        fun getCollectionReferenceForCards(): CollectionReference {
            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            return FirebaseFirestore.getInstance().collection("cards")
                .document(currentUser?.uid ?: "")
                .collection("my_cards")
        }

        fun getCollectionReferenceForNotes(): CollectionReference {
            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            return FirebaseFirestore.getInstance().collection("cards")
                .document(currentUser?.uid ?: "")
                .collection("my_notes")
        }






    }



}