package com.app.callofcthulhu.fragments

import SharedViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.app.callofcthulhu.CardDetailsActivity
import com.app.callofcthulhu.R
import com.app.callofcthulhu.Utility


class TraitFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var silaEditText: EditText
    private lateinit var kondycjaEditText: EditText
    private lateinit var bcialaEditText: EditText
    private lateinit var zrecznoscEditText: EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_trait, container, false)

        silaEditText = view.findViewById(R.id.card_sila)
        kondycjaEditText = view.findViewById(R.id.card_kondycja)
        bcialaEditText = view.findViewById(R.id.card_bciala)
        zrecznoscEditText = view.findViewById(R.id.card_zrecznosc)


        val buttonSila = view.findViewById<ImageButton>(R.id.button_sila)
        val buttonKondycja = view.findViewById<ImageButton>(R.id.button_kondycja)
        val buttonBciala = view.findViewById<ImageButton>(R.id.button_bciala)
        val buttonZrecznosc = view.findViewById<ImageButton>(R.id.button_zrecznosc)


        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        //przenosi dane do ViewModelu
        fun attachTextWatcher(editText: EditText, fieldName: String) {
            editText.addTextChangedListener { editable ->
                val input = editable.toString()
                val value = if (input.isNotBlank()) input.toIntOrNull() ?: 0 else 0
                sharedViewModel.updateCardField(fieldName, value)
            }
        }

// Wywołanie funkcji attachTextWatcher dla każdego EditText
        attachTextWatcher(silaEditText, "sila")
        attachTextWatcher(kondycjaEditText, "kondycja")
        attachTextWatcher(bcialaEditText, "bCiala")
        attachTextWatcher(zrecznoscEditText, "zrecznosc")



        val buttonClickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.button_sila -> {
                    val randomNumber = (3..18).random()*5
                    silaEditText.setText(randomNumber.toString())
                }

                R.id.button_kondycja -> {
                    val randomNumber = (3..18).random()*5
                    kondycjaEditText.setText(randomNumber.toString())
                }
                R.id.button_bciala -> {
                    val randomNumber = ((2..12).random()+6)*5
                    bcialaEditText.setText(randomNumber.toString())
                }
                R.id.button_zrecznosc -> {
                    val randomNumber = (3..18).random()*5
                    zrecznoscEditText.setText(randomNumber.toString())
                }

            }
        }
        //działanie do guzików
        buttonSila.setOnClickListener(buttonClickListener)
        buttonKondycja.setOnClickListener(buttonClickListener)
        buttonBciala.setOnClickListener(buttonClickListener)
        buttonZrecznosc.setOnClickListener(buttonClickListener)


        val id = CardDetailsActivity.docId
        if (id.isNotEmpty()) {
            val docReference = Utility.getCollectionReferenceForNotes().document(id)
            docReference.get().addOnSuccessListener { document ->
                if (document.exists()) {

                    val sila = document.getLong("sila")?.toInt()
                    val kondycja = document.getLong("kondycja")?.toInt()
                    val bciala = document.getLong("bciala")?.toInt()
                    val zrecznosc = document.getLong("zrecznosc")?.toInt()

                    // Przypisanie pobranych danych do pól EditText w fragmencie

                    if (sila != null) {
                        silaEditText.setText(sila.toString())
                    }
                    if (kondycja != null) {
                        kondycjaEditText.setText(kondycja.toString())
                    }
                    if (bciala != null) {
                        bcialaEditText.setText(bciala.toString())
                    }
                    if (zrecznosc != null) {
                        zrecznoscEditText.setText(zrecznosc.toString())
                    }
                }
            }.addOnFailureListener { exception ->
                // Obsługa błędu pobierania danych z Firestore
            }
        }


        return view
    }


}