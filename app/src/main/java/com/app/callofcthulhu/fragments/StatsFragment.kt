package com.app.callofcthulhu.fragments

import SharedViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.app.callofcthulhu.CardDetailsActivity
import com.app.callofcthulhu.R
import com.app.callofcthulhu.Utility


class StatsFragment : Fragment() {



    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stats, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)





        val id = CardDetailsActivity.docId
        if (id.isNotEmpty()) {
            val docReference = Utility.getCollectionReferenceForCards().document(id)
            docReference.get().addOnSuccessListener { document ->
                if (document.exists()) {



                    // Przypisanie pobranych danych do pól EditText w fragmencie


                }
            }.addOnFailureListener { exception ->
                // Obsługa błędu pobierania danych z Firestore
            }
        }




        return view
    }


}