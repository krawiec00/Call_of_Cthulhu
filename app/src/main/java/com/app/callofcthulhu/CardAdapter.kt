package com.app.callofcthulhu

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.callofcthulhu.CardAdapter.CardViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CardAdapter(options: FirestoreRecyclerOptions<Card>, var context: Context) :
    FirestoreRecyclerAdapter<Card, CardViewHolder>(options) {


    override fun onBindViewHolder(holder: CardViewHolder, position: Int, card: Card) {
        holder.titleTextView.text = card.title
        holder.contentTextView.text = card.content
        holder.itemView.setOnClickListener() {
            val intent = Intent(context, CardDetailsActivity::class.java)
            intent.putExtra("title", card.title)
            intent.putExtra("content", card.content)
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
        var titleTextView: TextView
        var contentTextView: TextView

        init {
            titleTextView = itemView.findViewById(R.id.card_title_view)
            contentTextView = itemView.findViewById(R.id.card_content_view)
        }
    }
}