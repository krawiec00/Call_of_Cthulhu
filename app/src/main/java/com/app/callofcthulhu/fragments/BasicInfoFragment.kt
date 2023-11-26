package com.app.callofcthulhu.fragments

import SharedViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.app.callofcthulhu.CardDetailsActivity
import com.app.callofcthulhu.R
import com.app.callofcthulhu.Utility.Companion.getCollectionReferenceForNotes


class BasicInfoFragment : Fragment() {


    private lateinit var imieEditText: EditText
    private lateinit var nazwiskoEditText: EditText
    private lateinit var profesjaEditText: EditText
    private lateinit var wiekEditText: EditText
    private lateinit var plecEditText: EditText
    private lateinit var mieszkanieEditText: EditText
    private lateinit var urodzenieEditText: EditText


    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_basic_info, container, false)



        imieEditText = view.findViewById(R.id.card_imie)
        nazwiskoEditText = view.findViewById(R.id.card_nazwisko)
        profesjaEditText = view.findViewById(R.id.card_profesja)
        wiekEditText = view.findViewById(R.id.card_wiek)
        plecEditText = view.findViewById(R.id.card_plec)
        mieszkanieEditText = view.findViewById(R.id.card_mZamieszkania)
        urodzenieEditText = view.findViewById(R.id.card_mUrodzenia)



        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        fun attachTextWatcher(editText: EditText, fieldName: String) {
            editText.addTextChangedListener { editable ->
                sharedViewModel.updateCardField(fieldName, editable.toString())
            }
        }

// Wywołanie funkcji attachTextWatcher dla każdego EditText
        attachTextWatcher(imieEditText, "imie")
        attachTextWatcher(nazwiskoEditText, "nazwisko")
        attachTextWatcher(profesjaEditText, "profesja")
        attachTextWatcher(wiekEditText, "wiek")
        attachTextWatcher(plecEditText, "plec")
        attachTextWatcher(mieszkanieEditText, "mZamieszkania")
        attachTextWatcher(urodzenieEditText, "mUrodzenia")



        val id = CardDetailsActivity.docId
        if (id.isNotEmpty()) {
            val docReference = getCollectionReferenceForNotes().document(id)
            docReference.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val imie = document.getString("imie")
                    val nazwisko = document.getString("nazwisko")
                    val profesja = document.getString("profesja")
                    val wiek = document.getString("wiek")
                    val plec = document.getString("plec")
                    val mzamieszkania = document.getString("mzamieszkania")
                    val murodzenia = document.getString("murodzenia")

                    // Przypisanie pobranych danych do pól EditText w fragmencie
                    imieEditText.setText(imie)
                    nazwiskoEditText.setText(nazwisko)
                    profesjaEditText.setText(profesja)
                    wiekEditText.setText(wiek)
                    plecEditText.setText(plec)
                    mieszkanieEditText.setText(mzamieszkania)
                    urodzenieEditText.setText(murodzenia)

                }
            }.addOnFailureListener { exception ->
                // Obsługa błędu pobierania danych z Firestore
            }
        }






        return view
    }


}