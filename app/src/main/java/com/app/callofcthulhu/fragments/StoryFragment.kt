package com.app.callofcthulhu.fragments

import SharedViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.app.callofcthulhu.CardDetailsActivity
import com.app.callofcthulhu.R
import com.app.callofcthulhu.Utility.Companion.getCollectionReferenceForCards


class StoryFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var opisEditText: EditText
    private lateinit var ideologiaEditText: EditText
    private lateinit var osobyEditText: EditText
    private lateinit var miejscaEditText: EditText
    private lateinit var rzeczyEditText: EditText
    private lateinit var przymiotyEditText: EditText
    private lateinit var urazyEditText: EditText
    private lateinit var fobieEditText: EditText
    private lateinit var ksiegiEditText: EditText
    private lateinit var istotyEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_story, container, false)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        opisEditText = view.findViewById(R.id.card_opis)
        ideologiaEditText = view.findViewById(R.id.card_ideologia)
        osobyEditText = view.findViewById(R.id.card_wosoby)
        miejscaEditText = view.findViewById(R.id.card_miejsca)
        rzeczyEditText = view.findViewById(R.id.card_rzeczyo)
        przymiotyEditText = view.findViewById(R.id.card_przymioty)
        urazyEditText = view.findViewById(R.id.card_urazy)
        fobieEditText = view.findViewById(R.id.card_fobie)
        ksiegiEditText = view.findViewById(R.id.card_ksiegi)
        istotyEditText = view.findViewById(R.id.card_istoty)


        attachTextWatcher(opisEditText, "opis")
        attachTextWatcher(ideologiaEditText, "ideologia")
        attachTextWatcher(osobyEditText, "osoby")
        attachTextWatcher(miejscaEditText, "miejsca")
        attachTextWatcher(rzeczyEditText, "rzeczy")
        attachTextWatcher(przymiotyEditText, "przymioty")
        attachTextWatcher(urazyEditText, "urazy")
        attachTextWatcher(fobieEditText, "fobie")
        attachTextWatcher(ksiegiEditText, "ksiegi")
        attachTextWatcher(istotyEditText, "istoty")


        val id = CardDetailsActivity.docId
        if (id.isNotEmpty()) {
            getCollectionReferenceForCards().document(id).get().addOnSuccessListener { document ->
                document?.let {
                    if (it.exists()) {
                        val fields = mapOf(
                            "opis" to opisEditText,
                            "ideologia" to ideologiaEditText,
                            "osoby" to osobyEditText,
                            "miejsca" to miejscaEditText,
                            "rzeczy" to rzeczyEditText,
                            "przymioty" to przymiotyEditText,
                            "urazy" to urazyEditText,
                            "fobie" to fobieEditText,
                            "ksiegi" to ksiegiEditText,
                            "istoty" to istotyEditText
                        )

                        fields.forEach { (key, editText) ->
                            editText.setText(document.getString(key))
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                // Obsługa błędu pobierania danych z Firestore
            }
        }



        return view
    }

    fun attachTextWatcher(editText: EditText, fieldName: String) {
        editText.addTextChangedListener { editable ->
            sharedViewModel.updateCardField(fieldName, editable.toString())
        }
    }


}