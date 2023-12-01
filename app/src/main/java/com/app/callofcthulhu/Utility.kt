package com.app.callofcthulhu

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class Utility {

    companion object {
        fun getCollectionReferenceForCards(): CollectionReference {
            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            return FirebaseFirestore.getInstance().collection("cards")
                .document(currentUser?.uid ?: "")
                .collection("my_cards")
        }


    }



}