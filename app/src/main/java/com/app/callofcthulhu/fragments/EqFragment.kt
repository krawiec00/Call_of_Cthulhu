package com.app.callofcthulhu.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.callofcthulhu.CardDetailsActivity
import com.app.callofcthulhu.MyApp
import com.app.callofcthulhu.R
import com.app.callofcthulhu.SharedViewModel
import com.app.callofcthulhu.Spell
import com.app.callofcthulhu.SpellsAdapter
import com.app.callofcthulhu.SpellsList
import com.app.callofcthulhu.Utility
import com.app.callofcthulhu.Utility.Companion.getCollectionReferenceForCards
import com.app.callofcthulhu.Weapon
import com.app.callofcthulhu.WeaponsAdapter
import com.app.callofcthulhu.WeaponsList
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query


class EqFragment : Fragment() {

    val sharedViewModel = MyApp.sharedViewModel

    private lateinit var weapontBtn: Button
    private lateinit var spellBtn: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var weaponAdapter: WeaponsAdapter
    private lateinit var spellAdapter: SpellsAdapter
    private lateinit var eqList: LinearLayout
    private lateinit var editTextArray: Array<EditText>

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

        editTextArray = arrayOf(
            view.findViewById(R.id.poziomWydatkow),
            view.findViewById(R.id.gotowka),
            view.findViewById(R.id.dobytek),
            view.findViewById(R.id.ekwipunek)
        )

        attachTextWatcher(editTextArray[0], "poziomWydatkow")
        attachTextWatcher(editTextArray[1], "gotowka")
        attachTextWatcher(editTextArray[2], "dobytek")
        attachTextWatcher(editTextArray[3], "ekwipunek")


        if(id.isEmpty())
            eqList.visibility = View.GONE
        else
            readData()

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

    private fun attachTextWatcher(editText: EditText, fieldName: String) {
        editText.addTextChangedListener { editable ->
            sharedViewModel.updateCardField(fieldName, editable.toString())
        }
    }

    private fun readData() {
        val docReference = getCollectionReferenceForCards().document(id)
        docReference.get().addOnSuccessListener { document ->
            document?.let {
                if (it.exists()) {
                    val fields = mapOf(
                        "wydatki" to editTextArray[0],
                        "gotowka" to editTextArray[1],
                        "dobytek" to editTextArray[2],
                        "przedmioty" to editTextArray[3]
                    )

                    fields.forEach { (key, editText) ->
                        editText.setText(document.getString(key))
                    }
                }
            }
        }
    }



}
