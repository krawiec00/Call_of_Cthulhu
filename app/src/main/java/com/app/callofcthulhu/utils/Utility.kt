package com.app.callofcthulhu.utils


import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class Utility {

    companion object {
        fun getCollectionReferenceForCards(): CollectionReference {
            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            return FirebaseFirestore.getInstance().collection("cards")
                .document(currentUser?.uid ?: "")
                .collection("my_cards")
        }

        fun getCollectionReference(collection: String): CollectionReference {
            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            return FirebaseFirestore.getInstance().collection("cards")
                .document(currentUser?.uid ?: "")
                .collection(collection)
        }

        fun getCollectionReferenceForNotes(): CollectionReference {
            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            return FirebaseFirestore.getInstance().collection("cards")
                .document(currentUser?.uid ?: "")
                .collection("my_notes")
        }

        fun getCollectionReferenceForWeapons(): CollectionReference {
            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            return FirebaseFirestore.getInstance().collection("cards")
                .document(currentUser?.uid ?: "")
                .collection("my_weapons")
        }


        fun getCollectionReferenceForSpells(): CollectionReference {
            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            return FirebaseFirestore.getInstance().collection("cards")
                .document(currentUser?.uid ?: "")
                .collection("my_spells")
        }


        fun writeLogToFirebase(logName: String) {
            val user = FirebaseAuth.getInstance().currentUser
            val db = Firebase.firestore

            val logData = hashMapOf(
                "actionName" to logName,
                "timestamp" to FieldValue.serverTimestamp()
            )

            if (user != null) {
                db.collection("user_logs").document(user.uid)
                    .collection("actions").add(logData)
            }
        }


    }


}