package com.app.callofcthulhu

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class SpellsAdapter(options: FirestoreRecyclerOptions<Spell>, var context: Context) :
    FirestoreRecyclerAdapter<Spell, SpellsAdapter.SpellsViewHolder>(options) {


    override fun onBindViewHolder(holder: SpellsViewHolder, position: Int, spell: Spell) {
        holder.spellNameTextView.text = spell.nazwa
        holder.itemView.setOnClickListener(){
            val intent = Intent(context, SpellsDetailsActivity::class.java).apply {
                putExtra("nazwa", spell.nazwa)
                putExtra("czas", spell.czas)
                putExtra("koszt", spell.koszt)
                putExtra("opis", spell.opis)
                putExtra("added", true)
                val spellID = snapshots.getSnapshot(position).id
                putExtra("id", spellID)
            }
            context.startActivity(intent)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpellsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_spell_item, parent, false)
        return SpellsViewHolder(view)
    }

    class SpellsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val spellNameTextView: TextView = itemView.findViewById(R.id.spell_name)


    }
}
