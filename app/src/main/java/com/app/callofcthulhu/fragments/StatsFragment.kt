package com.app.callofcthulhu.fragments

import com.app.callofcthulhu.SharedViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.app.callofcthulhu.CardDetailsActivity
import com.app.callofcthulhu.R
import com.app.callofcthulhu.Utility


class StatsFragment : Fragment() {

    private lateinit var editTextArray: Array<EditText>
    private lateinit var buttonArray: Array<ImageButton>
    private lateinit var textViewArray: Array<Pair<TextView, TextView>>

    private lateinit var sharedViewModel: SharedViewModel
    val id = CardDetailsActivity.docId

    var randomNumber: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stats, container, false)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]


        editTextArray = arrayOf(
            view.findViewById(R.id.card_bronPalna),
            view.findViewById(R.id.card_biblioteki),
            view.findViewById(R.id.card_nasluchiwanie),
            view.findViewById(R.id.card_nawigacja),
            view.findViewById(R.id.card_perswazja),
            view.findViewById(R.id.card_pierwszaPomoc),
            view.findViewById(R.id.card_psychologia),
            view.findViewById(R.id.card_spostrzegawczość),
            view.findViewById(R.id.card_sztukaPrzetrwania),
            view.findViewById(R.id.card_wiedzaONaturze)
        )

        buttonArray = arrayOf(
            view.findViewById(R.id.button_bron),
            view.findViewById(R.id.button_biblioteka),
            view.findViewById(R.id.button_nasluchiwanie),
            view.findViewById(R.id.button_nawigacja),
            view.findViewById(R.id.button_perswazja),
            view.findViewById(R.id.button_pierwszaPomoc),
            view.findViewById(R.id.button_psychologia),
            view.findViewById(R.id.button_spostrzegawczosc),
            view.findViewById(R.id.button_sztukaPrzetrwania),
            view.findViewById(R.id.button_wiedzaONaturze)
        )

        textViewArray = arrayOf(
            Pair(view.findViewById(R.id.bronPalna), view.findViewById(R.id.bronPalna2)),
            Pair(view.findViewById(R.id.biblioteki), view.findViewById(R.id.biblioteki2)),
            Pair(view.findViewById(R.id.nasluchiwanie), view.findViewById(R.id.nasluchiwanie2)),
            Pair(view.findViewById(R.id.nawigacja), view.findViewById(R.id.nawigacja2)),
            Pair(view.findViewById(R.id.perswazja), view.findViewById(R.id.perswazja2)),
            Pair(view.findViewById(R.id.pierwszaPomoc), view.findViewById(R.id.pierwszaPomoc2)),
            Pair(view.findViewById(R.id.psychologia), view.findViewById(R.id.psychologia2)),
            Pair(view.findViewById(R.id.spostrzegawczość), view.findViewById(R.id.spostrzegawczość2)),
            Pair(view.findViewById(R.id.sztukaPrzetrwania), view.findViewById(R.id.sztukaPrzetrwania2)),
            Pair(view.findViewById(R.id.wiedzaONaturze), view.findViewById(R.id.wiedzaONaturze2))
        )

        attachTextWatcher(editTextArray[0], "Bron_Palna")
        attachTextWatcher(editTextArray[1], "Korzystanie_z_Bibliotek")
        attachTextWatcher(editTextArray[2], "Nasluchiwanie")
        attachTextWatcher(editTextArray[3], "Nawigacja")
        attachTextWatcher(editTextArray[4], "Perswazja")
        attachTextWatcher(editTextArray[5], "Pierwsza_Pomoc")
        attachTextWatcher(editTextArray[6], "Psychologia")
        attachTextWatcher(editTextArray[7], "Spostrzegawczosc")
        attachTextWatcher(editTextArray[8], "Sztuka_Przetrwania")
        attachTextWatcher(editTextArray[9], "Wiedza_o_Naturze")


        readData()

        setupListeners()

        //nasłuchiwanie guzików
        val buttonClickListener = View.OnClickListener { view ->
            val index = buttonArray.indexOf(view)
            if (index != -1) {
                handleButtonClick(editTextArray[index])
            }
        }

        buttonArray.forEach { button ->
            button.setOnClickListener(buttonClickListener)
        }



        return view
    }

    private fun readData() {
        val docReference = Utility.getCollectionReferenceForCards().document(id)
        docReference.get().addOnSuccessListener { document ->
                document?.let {
                    if (it.exists()) {
                        val fields = mapOf(
                            "bron_Palna" to editTextArray[0],
                            "korzystanie_z_Bibliotek" to editTextArray[1],
                            "nasluchiwanie" to editTextArray[2],
                            "nawigacja" to editTextArray[3],
                            "perswazja" to editTextArray[4],
                            "pierwsza_Pomoc" to editTextArray[5],
                            "psychologia" to editTextArray[6],
                            "spostrzegawczosc" to editTextArray[7],
                            "sztuka_Przetrwania" to editTextArray[8],
                            "wiedza_o_Naturze" to editTextArray[9]

                        )

                        fields.forEach { (key, editText) ->
                            val value = document.getLong(key)?.toInt()
                            editText.setText(value?.toString() ?: "") // Ustawienie wartości w polu EditText
                        }
                    }
                }
            }
    }


    private fun listenerHalfData(editText: EditText, textview1: TextView, textview2: TextView) {
        editText.addTextChangedListener { editable ->
            val input = editable.toString()
            var intValue = input.toIntOrNull()
                ?: 0 // Konwersja na Int lub 0, jeśli konwersja nie powiedzie się
            var intValue2 = input.toIntOrNull() ?: 0
            intValue /= 2
            textview1.text = intValue.toString()
            intValue2 /= 5
            textview2.text = intValue2.toString()
        }
    }

    private fun attachTextWatcher(editText: TextView, fieldName: String) {
        editText.addTextChangedListener { editable ->
            val input = editable.toString()
            val value = if (input.isNotBlank()) input.toIntOrNull() ?: 0 else 0
            sharedViewModel.updateCardField(fieldName, value)
        }
    }

    private fun setupListeners() {
        editTextArray.forEachIndexed { index, editText ->
            val pair = textViewArray[index]
            val textView1 = pair.first
            val textView2 = pair.second

            listenerHalfData(editText, textView1, textView2)
        }
    }

    private fun handleButtonClick(editText: EditText) {
        val value = editText.text.toString().toIntOrNull() ?: 0

        if(value<=50){ //sprawdzamy jak ma losować, tu zawsze się rozwinie
            randomNumber = (1..10).random() + value
            editText.setText(randomNumber.toString())
        }
        else //sprawdzamy
        {
            val check = (1..100).random()
            if(check>value)
            {
                randomNumber = (1..10).random() + value
                editText.setText(randomNumber.toString())
            }
            else
                Toast.makeText(context, "NIE MOŻNA ROZWINĄĆ bo $check", Toast.LENGTH_SHORT).show()

        }

    }


}