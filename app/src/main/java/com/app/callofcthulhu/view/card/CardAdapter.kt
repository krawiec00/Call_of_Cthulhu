package com.app.callofcthulhu.view.card

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.callofcthulhu.R
import com.app.callofcthulhu.view.card.CardAdapter.CardViewHolder
import com.app.callofcthulhu.model.data.Card
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CardAdapter(options: FirestoreRecyclerOptions<Card>, var context: Context) :
    FirestoreRecyclerAdapter<Card, CardViewHolder>(options) {

    override fun onBindViewHolder(holder: CardViewHolder, position: Int, card: Card) {
        holder.imieEditText.text = card.imie
        holder.nazwiskoEditText.text = card.nazwisko

        Glide.with(context)
            .load(card.imageUrl)
            // Ustawienie domyślnego obrazka
            .error(R.drawable.baseline_add_photo_alternate_24) // Ustawienie obrazka w przypadku błędu
            .into(holder.imageView)


        // Obsługa kliknięcia na element RecyclerView
        holder.itemView.setOnClickListener {
            val intent = Intent(context, CardDetailsActivity::class.java)
            intent.putExtra("imie", card.imie)
            intent.putExtra("nazwisko", card.nazwisko)
            val docId = this.snapshots.getSnapshot(position).id
            intent.putExtra("docId", docId)
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_card_item, parent, false)
        return CardViewHolder(view)
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imieEditText: TextView
        var nazwiskoEditText: TextView
        var imageView: ImageView

        init {
            imieEditText = itemView.findViewById(R.id.card_imie_view)
            nazwiskoEditText = itemView.findViewById(R.id.card_nazwisko_view)
            imageView = itemView.findViewById(R.id.mini_image)
        }
    }
}