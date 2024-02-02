package com.app.callofcthulhu.view.share

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.callofcthulhu.R
import com.app.callofcthulhu.model.data.Request
import com.app.callofcthulhu.utils.Utility
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RequestAdapter(options: FirestoreRecyclerOptions<Request>, var context: Context) :
    FirestoreRecyclerAdapter<Request, RequestAdapter.RequestViewHolder>(options) {

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int, request: Request) {
        holder.fromUserMail.text = request.userMail
        holder.cardName.text = request.cardName

        holder.btnAction.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                copyCard(request)
                deleteRequest(currentPosition)
            }
        }

        holder.btnDelete.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                deleteRequest(currentPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_request_item, parent, false)
        return RequestViewHolder(view)
    }

    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fromUserMail: TextView = itemView.findViewById(R.id.request_mail)
        val cardName: TextView = itemView.findViewById(R.id.request_card_name)
        val btnAction: ImageButton = itemView.findViewById(R.id.btnAction)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }


    private fun deleteRequest(position: Int) {
        val documentSnapshot = snapshots.getSnapshot(position)
        try {
            documentSnapshot.reference.delete()
                .addOnSuccessListener {
                }
                .addOnFailureListener { e ->
                    Log.e("TAG", "EXCEPTION: $e")
                }
        } catch (e: Exception) {
            Toast.makeText(context, "Wystąpił błąd: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


    private fun copyCard(request: Request) {
        val fromUserId = request.fromUserId
        val docId = request.docId

        if (fromUserId != null && docId != null) {
            FirebaseFirestore.getInstance().collection("cards")
                .document(fromUserId)
                .collection("my_cards").document(docId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val cardData = document.data
                        // Wywołanie saveCard z funkcją completion
                        saveCard(cardData) { newCardId ->
                            saveCardContent(request, "my_notes", newCardId)
                            saveCardContent(request, "my_weapons", newCardId)
                            saveCardContent(request, "my_spells", newCardId)
                            copySkills(fromUserId, docId, newCardId)
                            // ...dalsze operacje
                        }
                    } else {
                        // Obsługa sytuacji, gdy dokument nie istnieje
                    }
                }
                .addOnFailureListener {
                    // Obsługa błędu
                }


        }
    }

    private fun copySkills(fromUserId: String, sourceCardId: String, destinationCardId: String) {
        FirebaseFirestore.getInstance().collection("cards")
            .document(fromUserId)
            .collection("my_cards").document(sourceCardId)
            .collection("Skills") // Podkolekcja "Skills" w źródłowej karcie
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val skillData = document.data
                    saveSkill(destinationCardId, destinationCardId, skillData)
                }
            }
            .addOnFailureListener {

            }
    }

    private fun saveSkill(
        destinationCardId: String,
        skillDocumentName: String,
        skillData: Map<String, Any>?
    ) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("cards")
            .document(currentUserId)
            .collection("my_cards").document(destinationCardId)
            .collection("Skills")
            .document(skillDocumentName)
            .set(skillData!!)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }

    private fun saveCard(cardData: Map<String, Any>?, completion: (String) -> Unit) {

        val newDocRef = Utility.getCollectionReferenceForCards().document()

        newDocRef.set(cardData!!)
            .addOnSuccessListener {
                Toast.makeText(context, "Dodano kartę", Toast.LENGTH_SHORT).show()
                completion(newDocRef.id) // Zwraca ID nowo utworzonego dokumentu
            }
            .addOnFailureListener {
                // Obsługa błędu
            }
    }


    private fun saveCardContent(request: Request, kolekcja: String, newCardId: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val fromUserId = request.fromUserId

        if (fromUserId != null) {
            FirebaseFirestore.getInstance().collection("cards")
                .document(fromUserId)
                .collection(kolekcja)
                .whereEqualTo("id", request.docId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val data = document.data?.toMutableMap()
                        data?.put("id", newCardId) // Zmień pole id na id nowo utworzonej karty

                        FirebaseFirestore.getInstance().collection("cards")
                            .document(currentUserId)
                            .collection(kolekcja)
                            .document() // Tworzy nowy dokument z zaktualizowanymi danymi
                            .set(data!!)
                            .addOnSuccessListener {
                                // Sukces, dokument został skopiowany i zaktualizowany
                            }
                            .addOnFailureListener {
                                // Obsługa błędu
                            }
                    }
                }
                .addOnFailureListener {
                    // Obsługa błędu
                }
        }
    }


}
