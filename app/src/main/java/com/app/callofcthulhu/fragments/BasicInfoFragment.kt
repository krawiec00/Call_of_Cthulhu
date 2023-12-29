package com.app.callofcthulhu.fragments

import com.app.callofcthulhu.SharedViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.app.callofcthulhu.CardDetailsActivity
import com.app.callofcthulhu.R
import com.app.callofcthulhu.Utility.Companion.getCollectionReferenceForCards


class BasicInfoFragment : Fragment() {


    private lateinit var imieEditText: EditText
    private lateinit var nazwiskoEditText: EditText
    private lateinit var profesjaEditText: EditText
    private lateinit var professionSpinner: Spinner
    private lateinit var wiekEditText: EditText
    private lateinit var plecEditText: EditText
    private lateinit var mieszkanieEditText: EditText
    private lateinit var urodzenieEditText: EditText
    val id = CardDetailsActivity.docId

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



        fun setupSpinner() {
            // Inicjalizacja Spinnera i adaptera
            professionSpinner = view.findViewById(R.id.card_spinner_profesja)
            val professions = arrayOf("Detektyw Policyjny", "Duchowny", "Zolnierz") // Twoja lista profesji
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, professions)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            professionSpinner.adapter = adapter

            // Listener dla wyboru elementu w Spinnerze
            professionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedProfession = professions[position]
                    sharedViewModel.updateCardField("profesja", selectedProfession)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Obsługa sytuacji, gdy nie jest wybrany żaden element
                }
            }
            }


        setupSpinner()




        if (id.isNotEmpty()) {
            professionSpinner.visibility = View.GONE
            profesjaEditText.visibility = View.VISIBLE
            val docReference = getCollectionReferenceForCards().document(id)
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