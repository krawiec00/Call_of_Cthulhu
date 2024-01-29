package com.app.callofcthulhu.model.repository


import android.net.Uri
import com.app.callofcthulhu.view.card.CardDetailsActivity
import com.app.callofcthulhu.utils.Utility
import com.app.callofcthulhu.utils.Utility.Companion.getCollectionReferenceForCards
import com.app.callofcthulhu.model.data.Card
import com.app.callofcthulhu.utils.SharedViewModelInstance
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.storage


class CardRepository {


    fun saveCardToFireBase(card: Card, imageUri: Uri?) {
        val sharedViewModel = SharedViewModelInstance.instance
        val id = CardDetailsActivity.docId
        val documentReference: DocumentReference = if (id.isNotEmpty()) {
            getCollectionReferenceForCards().document(id)
        } else {
            getCollectionReferenceForCards().document()
        }
        documentReference.set(card)
        val newDocId = documentReference.id
        imageUri?.let { uri ->
            uploadImage(uri, newDocId) { imageUrl ->
                card.imageUrl = imageUrl
                documentReference.set(card)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            sharedViewModel.updateImageUri(null)
                        }
                    }
            }
        } ?: run {
            documentReference.set(card)
        }
    }

    private fun uploadImage(file: Uri, fileName: String, callback: (String?) -> Unit) {

        try {
            val userId = Firebase.auth.currentUser?.uid.toString()
            val storageReference = Firebase.storage.reference
            val ref = storageReference.child("$userId/$fileName")

            ref.putFile(file)
                .addOnSuccessListener { uploadTask ->
                    uploadTask.storage.downloadUrl
                        .addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            callback(imageUrl)
                        }
                        .addOnFailureListener {
                            callback(null) // Wywołanie funkcji zwrotnej z wartością null w przypadku niepowodzenia
                        }
                }
                .addOnFailureListener {
                    callback(null) // Wywołanie funkcji zwrotnej z wartością null w przypadku niepowodzenia
                }
        } catch (securityException: SecurityException) {
            securityException.printStackTrace()
        }
    }

    fun deleteCardFromFirebase() {
        val documentReference: DocumentReference =
            getCollectionReferenceForCards().document(CardDetailsActivity.docId)
        documentReference.delete()
        Utility.writeLogToFirebase("Usunięcie karty")
    }

    fun deleteImage() {
        val userId = Firebase.auth.currentUser?.uid.toString()
        val storage = Firebase.storage
        val storageRef = storage.reference.child("$userId/${CardDetailsActivity.docId}")

        storageRef.delete()
            .addOnSuccessListener {
            }
            .addOnFailureListener { exception ->

            }
    }

    fun deleteFromFirebase(cardId: String, dataReference: String) {
        val collection = Utility.getCollectionReference(dataReference)
        collection.whereEqualTo("id", cardId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    document.reference.delete()
                }
            }
        }
    }


}