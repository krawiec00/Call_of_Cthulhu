package com.app.callofcthulhu.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.callofcthulhu.view.card.CardDetailsActivity
import com.app.callofcthulhu.R
import com.app.callofcthulhu.model.data.SkillItem
import com.app.callofcthulhu.model.data.Skills
import com.app.callofcthulhu.utils.SharedViewModelInstance
import com.app.callofcthulhu.utils.Utility
import com.app.callofcthulhu.view.points.InterestPointsActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class StatsFragment : Fragment() {

    val sharedViewModel = SharedViewModelInstance.instance
    val id = CardDetailsActivity.docId
    private lateinit var recyclerView: RecyclerView
    private var skillsAdapter: SkillsAdapter? = null
    private val fieldNamesMap = Utility.fieldNamesMap


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_newstats, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)

        recyclerView.layoutManager = GridLayoutManager(context, 2)
        skillsAdapter = SkillsAdapter(requireContext(), emptyList())
        recyclerView.adapter = skillsAdapter
        // initializeRecyclerView()
        fetchSkillsData()

        return view
    }

    private fun initializeRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun fetchSkillsData() {
        // Pseudokod do pobierania danych z Firestore
        val docRef = Utility.getCollectionReferenceForCards().document(CardDetailsActivity.docId)
            .collection("Skills")
            .document(CardDetailsActivity.docId)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val skillItems = transformToSkillItems(document)
                val sortedSkills = skillItems.sortedBy { it.friendlyName }
                skillsAdapter = SkillsAdapter(requireContext(), sortedSkills)
                recyclerView.adapter = skillsAdapter
                document.data?.forEach { (key, value) ->
                    val skillValue = (value as Number).toInt()
                    sharedViewModel.updateSkillField(key, skillValue)
                }
            }
        }
    }

    private fun transformToSkillItems(document: DocumentSnapshot): List<SkillItem> {
        val skillItems = mutableListOf<SkillItem>()
        // Iteruj przez pola dokumentu i przekształć je na SkillItem
        document.data?.forEach { (key, value) ->
            val friendlyName = fieldNamesMap[key] ?: key // Użyj mapy do tłumaczenia
            val skillValue = (value as Number).toInt()
            skillItems.add( SkillItem(key, friendlyName, skillValue)) // Dodaj techniczną i przyjazną nazwę
        }
        return skillItems
    }


}