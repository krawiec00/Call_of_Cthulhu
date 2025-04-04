package com.app.callofcthulhu.model.repository


import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import com.app.callofcthulhu.view.card.CardDetailsActivity
import com.app.callofcthulhu.utils.Utility
import com.app.callofcthulhu.utils.Utility.Companion.getCollectionReferenceForCards
import com.app.callofcthulhu.model.data.Card
import com.app.callofcthulhu.model.data.Skills
import com.app.callofcthulhu.utils.SharedViewModelInstance
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import java.text.SimpleDateFormat
import java.util.Date


class CardRepository {


    @SuppressLint("SimpleDateFormat")
    fun saveCardToFireBase(card: Card, imageUri: Uri?) {
        val sharedViewModel = SharedViewModelInstance.instance
        val id = CardDetailsActivity.docId

        val currentDate = Date()

// Tworzenie obiektu SimpleDateFormat z określonym formatem
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

// Formatowanie daty jako string zgodnie z określonym szablonem
        val dateString = dateFormat.format(currentDate)

        val documentReference: DocumentReference = if (id.isNotEmpty()) {
            getCollectionReferenceForCards().document(id)
        } else {
            getCollectionReferenceForCards().document()
        }
        val newDocId = documentReference.id
        imageUri?.let { uri ->
            uploadImage(uri, newDocId) { imageUrl ->
                card.imageUrl = imageUrl
                card.timestamp = dateString
                documentReference.set(card)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            sharedViewModel.updateImageUri(null)
                            val skills = sharedViewModel.skills.value ?: return@addOnCompleteListener
                            saveSkillsToFirebase(skills, newDocId)
                        }
                    }
            }
        } ?: run {
            card.timestamp = dateString
            documentReference.set(card).addOnSuccessListener {
                val skills = sharedViewModel.skills.value ?: return@addOnSuccessListener
                saveSkillsToFirebase(skills, newDocId)
            }
        }
    }

    private fun saveSkillsToFirebase(skills: Skills, cardId: String) {
        val skillsRef = getCollectionReferenceForCards().document(cardId).collection("Skills").document(cardId)
        skillsRef.set(skills)
            .addOnSuccessListener {
                // Obsługa sukcesu
            }
            .addOnFailureListener {
                // Obsługa błędu
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
        val skillReference: DocumentReference = getCollectionReferenceForCards().document(CardDetailsActivity.docId).collection("Skills")
            .document(CardDetailsActivity.docId)
        skillReference.delete().addOnSuccessListener {
            val documentReference: DocumentReference =
                getCollectionReferenceForCards().document(CardDetailsActivity.docId)
            documentReference.delete()
            Utility.writeLogToFirebase("Usunięcie karty")
        }
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

    fun deleteShareRequest(cardId: String){
        val db = FirebaseFirestore.getInstance()
        val requestsCollectionRef = db.collection("requests")

        // Tworzenie zapytania z warunkiem, aby pobrać dokumenty, których pole "docId" jest równe wartości docIdToDelete
        requestsCollectionRef.whereEqualTo("docId", cardId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Iteracja przez wyniki zapytania
                for (document in querySnapshot.documents) {
                    // Usunięcie każdego dokumentu z kolekcji "requests"
                    document.reference.delete()
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener { e ->
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Błąd podczas pobierania dokumentów: $e")
            }
    }


}