package com.app.callofcthulhu.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.callofcthulhu.Card
import com.app.callofcthulhu.CardAdapter
import com.app.callofcthulhu.CardDetailsActivity
import com.app.callofcthulhu.R
import com.app.callofcthulhu.Spell
import com.app.callofcthulhu.SpellsAdapter
import com.app.callofcthulhu.SpellsList
import com.app.callofcthulhu.Utility
import com.app.callofcthulhu.Utility.Companion.getCollectionReferenceForCards
import com.app.callofcthulhu.Weapon
import com.app.callofcthulhu.WeaponsAdapter
import com.app.callofcthulhu.WeaponsDetailsActivity
import com.app.callofcthulhu.WeaponsList
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject


class EqFragment : Fragment() {

    private lateinit var weapontBtn: Button
    private lateinit var spellBtn: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    lateinit var weaponAdapter: WeaponsAdapter
    lateinit var spellAdapter: SpellsAdapter
    lateinit var eqList: LinearLayout

    val id = CardDetailsActivity.docId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_eq, container, false)


        weapontBtn = view.findViewById(R.id.weapon_btn)
        spellBtn = view.findViewById(R.id.spell_btn)

        eqList = view.findViewById(R.id.lista)

        weapontBtn.setOnClickListener {
            val intent = Intent(activity, WeaponsList::class.java)
            startActivity(intent)
        }

        spellBtn.setOnClickListener(){
            val intent = Intent(activity, SpellsList::class.java)
            startActivity(intent)
        }

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView2 = view.findViewById(R.id.recycler_view2)

        if(id.isEmpty())
            eqList.visibility = View.GONE


        setupRecyclerView()
        setupRecyclerView2()



        return view
    }


    private fun setupRecyclerView() {
        val userId = CardDetailsActivity.docId
        val query: Query = Utility.getCollectionReferenceForWeapons()
                .orderBy("nazwa", Query.Direction.DESCENDING)
                .whereEqualTo("id", userId)

        val options: FirestoreRecyclerOptions<Weapon> = FirestoreRecyclerOptions.Builder<Weapon>()
            .setQuery(query, Weapon::class.java)
            .build()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        weaponAdapter = WeaponsAdapter(options, requireContext())
        recyclerView.adapter = weaponAdapter
    }

    private fun setupRecyclerView2() {
        val userId = CardDetailsActivity.docId
        val query: Query = Utility.getCollectionReferenceForSpells()
            .orderBy("nazwa", Query.Direction.DESCENDING)
            .whereEqualTo("id", userId)

        val options: FirestoreRecyclerOptions<Spell> = FirestoreRecyclerOptions.Builder<Spell>()
            .setQuery(query, Spell::class.java)
            .build()
        recyclerView2.layoutManager = LinearLayoutManager(requireContext())
        spellAdapter = SpellsAdapter(options, requireContext())
        recyclerView2.adapter = spellAdapter
    }

    override fun onStart() {
        super.onStart()
        weaponAdapter.startListening()
        spellAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        weaponAdapter.stopListening()
        spellAdapter.stopListening()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        weaponAdapter.notifyDataSetChanged()
        spellAdapter.notifyDataSetChanged()
    }




}
