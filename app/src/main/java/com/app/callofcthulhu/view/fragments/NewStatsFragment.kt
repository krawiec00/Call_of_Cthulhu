package com.app.callofcthulhu.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.app.callofcthulhu.view.card.CardDetailsActivity
import com.app.callofcthulhu.view.points.InterestPointsActivity
import com.app.callofcthulhu.view.points.ProfessionPointsActivity
import com.app.callofcthulhu.R
import com.app.callofcthulhu.utils.SharedViewModelInstance


class NewStatsFragment : Fragment() {


    val sharedViewModel = SharedViewModelInstance.instance

    val id = CardDetailsActivity.docId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stats2, container, false)


        val professionBtn = view.findViewById<Button>(R.id.klasa)

        professionBtn.setOnClickListener() {
            val intent = Intent(requireContext(), ProfessionPointsActivity::class.java)
            intent.putExtra("sila", sharedViewModel.card.value?.sila)
            intent.putExtra("wyksztalcenie", sharedViewModel.card.value?.wyksztalcenie)
            intent.putExtra("profesja", sharedViewModel.card.value?.profesja)
            startActivity(intent)
        }

        val zBtn = view.findViewById<Button>(R.id.zainteresowanie)

        zBtn.setOnClickListener() {
            val intent = Intent(requireContext(), InterestPointsActivity::class.java)
            intent.putExtra("inteligencja", sharedViewModel.card.value?.inteligencja)
            intent.putExtra("profesja", sharedViewModel.card.value?.profesja)
            startActivity(intent)
        }

        return view
    }


//


}

