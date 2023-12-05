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




















//class WeaponsAdapter(
//    private val weaponsList: List<Weapon>,
//    private val onClickListener: (Weapon) -> Unit
//) : RecyclerView.Adapter<WeaponsAdapter.ViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_weapon_item, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val weapon = weaponsList[position] // Pobierz obiekt broni z listy broni
//        holder.itemView.setOnClickListener {
//            val intent = Intent(holder.itemView.context, WeaponsDetailsActivity::class.java)
//            intent.putExtra("id", weapon.id) // Przekazanie ID broni do nowej aktywności
//            intent.putExtra("name", weapon.name)
//            holder.itemView.context.startActivity(intent)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return weaponsList.size
//    }
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val weaponNameTextView: TextView = itemView.findViewById(R.id.weapon_name)
//
//        fun bind(weapon: Weapon, onClickListener: (Weapon) -> Unit) {
//            weaponNameTextView.text = weapon.name
//
//            itemView.setOnClickListener {
//                onClickListener(weapon)
//            }
//        }
//    }
//}

//override fun onBindViewHolder(holder: WeaponsAdapter.ViewHolder, position: Int) {
//    val weapon = weaponsList[position]
//    holder.bind(weapon, onClickListener)
//}