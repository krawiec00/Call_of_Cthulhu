package com.app.callofcthulhu

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import io.grpc.Context

class Utility {

    companion object {
        fun getCollectionReferenceForNotes(): CollectionReference {
            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            return FirebaseFirestore.getInstance().collection("cards")
                .document(currentUser?.uid ?: "")
                .collection("my_cards")
        }
    }



}