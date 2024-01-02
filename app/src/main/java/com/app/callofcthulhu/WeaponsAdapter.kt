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


class WeaponsAdapter(options: FirestoreRecyclerOptions<Weapon>, var context: Context) :
    FirestoreRecyclerAdapter<Weapon, WeaponsAdapter.WeaponsViewHolder>(options) {


    override fun onBindViewHolder(holder: WeaponsViewHolder, position: Int, weapon: Weapon) {
        holder.weaponNameTextView.text = weapon.nazwa
        holder.itemView.setOnClickListener(){
            val intent = Intent(context, WeaponsDetailsActivity::class.java).apply {
                putExtra("nazwa", weapon.nazwa)
                putExtra("obrazenia", weapon.obrazenia)
                putExtra("ataki", weapon.ataki)
                putExtra("koszt", weapon.koszt)
                putExtra("pociski", weapon.pociski)
                putExtra("umiejetnosc", weapon.umiejetnosc)
                putExtra("zasieg", weapon.zasieg)
                putExtra("zawodnosc", weapon.zawodnosc)
                putExtra("added", true)
               val weaponID = snapshots.getSnapshot(position).id
                putExtra("id", weaponID)
            }
            context.startActivity(intent)
        }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeaponsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_weapon_item, parent, false)
        return WeaponsViewHolder(view)
    }

     class WeaponsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weaponNameTextView: TextView = itemView.findViewById(R.id.weapon_name)


    }
}
