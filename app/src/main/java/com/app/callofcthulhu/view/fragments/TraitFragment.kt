package com.app.callofcthulhu.view.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.app.callofcthulhu.view.card.CardDetailsActivity
import com.app.callofcthulhu.utils.MyApp
import com.app.callofcthulhu.R
import com.app.callofcthulhu.utils.SharedViewModelInstance
import com.app.callofcthulhu.utils.Utility


class TraitFragment : Fragment() {

    //    val sharedViewModel = MyApp.sharedViewModel
    val sharedViewModel = SharedViewModelInstance.instance
    private lateinit var silaEditText: EditText
    private lateinit var silaTextView: TextView
    private lateinit var sila2TextView: TextView

    private lateinit var kondycjaEditText: EditText
    private lateinit var kondycjaTextView: TextView
    private lateinit var kondycja2TextView: TextView

    private lateinit var bcialaEditText: EditText
    private lateinit var bcialaTextView: TextView
    private lateinit var bciala2TextView: TextView

    private lateinit var zrecznoscEditText: EditText
    private lateinit var zrecznoscTextView: TextView
    private lateinit var zrecznosc2TextView: TextView

    private lateinit var wygladEditText: EditText
    private lateinit var wygladTextView: TextView
    private lateinit var wyglad2TextView: TextView

    private lateinit var intEditText: EditText
    private lateinit var intTextView: TextView
    private lateinit var int2TextView: TextView

    private lateinit var mocEditText: EditText
    private lateinit var mocTextView: TextView
    private lateinit var moc2TextView: TextView

    private lateinit var wykEditText: EditText
    private lateinit var wykTextView: TextView
    private lateinit var wyk2TextView: TextView

    private lateinit var zycieEditText: EditText
    private lateinit var poczytalnoscEditText: EditText
    private lateinit var szczecieEditText: EditText
    private lateinit var magiaEditText: EditText

    private lateinit var maxZycieTextView: TextView
    private lateinit var maxMagiaTextView: TextView

    private lateinit var ruchEditText: EditText
    private lateinit var ranaCheckBox: CheckBox

    private lateinit var buttonArray: Array<ImageButton>
    private lateinit var editTextArray: Array<EditText>

    val id = CardDetailsActivity.docId

    private var maxzycie: Int = 0

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
        wygladEditText = view.findViewById(R.id.card_wyglad)
        intEditText = view.findViewById(R.id.card_inteligencja)
        mocEditText = view.findViewById(R.id.card_moc)
        wykEditText = view.findViewById(R.id.card_wyk)
        zycieEditText = view.findViewById(R.id.card_zycie)
        poczytalnoscEditText = view.findViewById(R.id.card_poczytalnosc)
        szczecieEditText = view.findViewById(R.id.card_szczescie)
        magiaEditText = view.findViewById(R.id.card_magia)
        ruchEditText = view.findViewById(R.id.card_ruch)
        ranaCheckBox = view.findViewById(R.id.rana_checkbox)

        //pola na wartości połowiczne
        silaTextView = view.findViewById(R.id.sila)
        sila2TextView = view.findViewById(R.id.sila2)

        kondycjaTextView = view.findViewById(R.id.kondycja)
        kondycja2TextView = view.findViewById(R.id.kondycja2)

        bcialaTextView = view.findViewById(R.id.bciala)
        bciala2TextView = view.findViewById(R.id.bciala2)

        zrecznoscTextView = view.findViewById(R.id.zrecznosc)
        zrecznosc2TextView = view.findViewById(R.id.zrecznosc2)

        wygladTextView = view.findViewById(R.id.wyglad)
        wyglad2TextView = view.findViewById(R.id.wyglad2)

        intTextView = view.findViewById(R.id.inteligencja)
        int2TextView = view.findViewById(R.id.inteligencja2)

        mocTextView = view.findViewById(R.id.moc)
        moc2TextView = view.findViewById(R.id.moc2)

        wykTextView = view.findViewById(R.id.wyk)
        wyk2TextView = view.findViewById(R.id.wyk2)

        maxZycieTextView = view.findViewById(R.id.maxzycie)
        maxMagiaTextView = view.findViewById(R.id.maxmagia)

        buttonArray = arrayOf(
            view.findViewById(R.id.button_1),
            view.findViewById(R.id.button_2),
            view.findViewById(R.id.button_3),
            view.findViewById(R.id.button_4),
            view.findViewById(R.id.button_5),
            view.findViewById(R.id.button_6),
            view.findViewById(R.id.button_7),
            view.findViewById(R.id.button_8)
        )

        editTextArray = arrayOf(
            silaEditText, kondycjaEditText, bcialaEditText, zrecznoscEditText,
            wygladEditText, intEditText, mocEditText, wykEditText,
            zycieEditText, poczytalnoscEditText, szczecieEditText, magiaEditText,
            ruchEditText
        )


// Wywołanie funkcji attachTextWatcher dla każdego EditText
        attachTextWatcher(silaEditText, "sila")
        attachTextWatcher(kondycjaEditText, "kondycja")
        attachTextWatcher(bcialaEditText, "bCiala")
        attachTextWatcher(zrecznoscEditText, "zrecznosc")
        attachTextWatcher(wygladEditText, "wyglad")
        attachTextWatcher(intEditText, "inteligencja")
        attachTextWatcher(mocEditText, "moc")
        attachTextWatcher(wykEditText, "wyksztalcenie")
        attachTextWatcher(zycieEditText, "zycie")
        attachTextWatcher(poczytalnoscEditText, "poczytalnosc")
        attachTextWatcher(szczecieEditText, "szczescie")
        attachTextWatcher(magiaEditText, "magia")
        attachTextWatcher(maxZycieTextView, "maxzycie")
        attachTextWatcher(maxMagiaTextView, "maxmagia")
        attachTextWatcher(ruchEditText, "ruch")
        attachCheckBoxListener(ranaCheckBox, "rana")

        //funckja dla wartości połowicznych lub 1/5 od cech
        listenerHalfData(silaEditText, silaTextView, sila2TextView)
        listenerHalfData(kondycjaEditText, kondycjaTextView, kondycja2TextView)
        listenerHalfData(bcialaEditText, bcialaTextView, bciala2TextView)
        listenerHalfData(zrecznoscEditText, zrecznoscTextView, zrecznosc2TextView)
        listenerHalfData(wygladEditText, wygladTextView, wyglad2TextView)
        listenerHalfData(intEditText, intTextView, int2TextView)
        listenerHalfData(mocEditText, mocTextView, moc2TextView)
        listenerHalfData(wykEditText, wykTextView, wyk2TextView)


        val buttonSila = view.findViewById<ImageButton>(R.id.button_sila)
        val buttonKondycja = view.findViewById<ImageButton>(R.id.button_kondycja)
        val buttonBciala = view.findViewById<ImageButton>(R.id.button_bciala)
        val buttonZrecznosc = view.findViewById<ImageButton>(R.id.button_zrecznosc)
        val buttonWyglad = view.findViewById<ImageButton>(R.id.button_wyglad)
        val buttonInteligencja = view.findViewById<ImageButton>(R.id.button_inteligencja)
        val buttonMoc = view.findViewById<ImageButton>(R.id.button_moc)
        val buttonWyksztalcenie = view.findViewById<ImageButton>(R.id.button_wyk)


        val buttonClickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.button_sila -> {
                    val randomNumber = (3..18).random() * 5
                    silaEditText.setText(randomNumber.toString())
                }

                R.id.button_kondycja -> {
                    val randomNumber = (3..18).random() * 5
                    kondycjaEditText.setText(randomNumber.toString())
                }

                R.id.button_bciala -> {
                    val randomNumber = ((2..12).random() + 6) * 5
                    bcialaEditText.setText(randomNumber.toString())
                }

                R.id.button_zrecznosc -> {
                    val randomNumber = (3..18).random() * 5
                    zrecznoscEditText.setText(randomNumber.toString())
                }

                R.id.button_wyglad -> {
                    val randomNumber = (3..18).random() * 5
                    wygladEditText.setText(randomNumber.toString())
                }

                R.id.button_inteligencja -> {
                    val randomNumber = ((2..12).random() + 6) * 5
                    intEditText.setText(randomNumber.toString())
                }

                R.id.button_moc -> {
                    val randomNumber = (3..18).random() * 5
                    mocEditText.setText(randomNumber.toString())
                }

                R.id.button_wyk -> {
                    val randomNumber = ((2..12).random() + 6) * 5
                    wykEditText.setText(randomNumber.toString())
                }

            }
        }
        //działanie do guzików losowania statystyk
        buttonSila.setOnClickListener(buttonClickListener)
        buttonKondycja.setOnClickListener(buttonClickListener)
        buttonBciala.setOnClickListener(buttonClickListener)
        buttonZrecznosc.setOnClickListener(buttonClickListener)
        buttonWyglad.setOnClickListener(buttonClickListener)
        buttonInteligencja.setOnClickListener(buttonClickListener)
        buttonMoc.setOnClickListener(buttonClickListener)
        buttonWyksztalcenie.setOnClickListener(buttonClickListener)


        //logika dla zycia, szczescia, poczytalnosci
        if (id.isEmpty()) {
            val randomNumber = (3..18).random() * 5
            szczecieEditText.setText(randomNumber.toString())

            zycieEditText.isFocusable = false
            magiaEditText.isFocusable = false
            poczytalnoscEditText.isFocusable = false
            ruchEditText.isFocusable = false

        }

        kondycjaEditText.addTextChangedListener {
            updateMaxZycie()
        }

        bcialaEditText.addTextChangedListener {
            updateMaxZycie()
            updateRuch()
        }

        mocEditText.addTextChangedListener { editable ->
            updateMaxMagia()
        }

        silaEditText.addTextChangedListener { editable ->
            updateRuch()
        }

        zrecznoscEditText.addTextChangedListener { editable ->
            updateRuch()
        }

        //odczyt zapisanych danych

        val firestoreFields = mapOf(
            "maxzycie" to maxZycieTextView,
            "maxmagia" to maxMagiaTextView,
            "sila" to silaEditText,
            "kondycja" to kondycjaEditText,
            "bciala" to bcialaEditText,
            "inteligencja" to intEditText,
            "wyglad" to wygladEditText,
            "moc" to mocEditText,
            "wyksztalcenie" to wykEditText,
            "zrecznosc" to zrecznoscEditText,
            "zycie" to zycieEditText,
            "poczytalnosc" to poczytalnoscEditText,
            "szczescie" to szczecieEditText,
            "magia" to magiaEditText,
            "rana" to ranaCheckBox,
            "ruch" to ruchEditText

        )

        if (id.isNotEmpty()) {
            val docReference = Utility.getCollectionReferenceForCards().document(id)
            docReference.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    for ((field, view) in firestoreFields) {
                        when (view) {
                            is CheckBox -> {
                                val value = document.getBoolean(field)
                                value?.let {
                                    view.isChecked = it
                                }
                            }

                            else -> {
                                val intValue = document.getLong(field)?.toInt()
                                intValue?.let {
                                    view.text = it.toString()
                                }
                            }
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                // Obsługa błędu pobierania danych z Firestore
            }


            zycieEditText.addTextChangedListener {
                statHealthController()
            }
            magiaEditText.addTextChangedListener {
                statMagicController()
            }

            buttonSila.visibility = View.GONE
            buttonKondycja.visibility = View.GONE
            buttonBciala.visibility = View.GONE
            buttonZrecznosc.visibility = View.GONE
            buttonWyglad.visibility = View.GONE
            buttonInteligencja.visibility = View.GONE
            buttonMoc.visibility = View.GONE
            buttonWyksztalcenie.visibility = View.GONE

            for (button in buttonArray) {
                button.visibility = View.VISIBLE
            }
            for (i in buttonArray.indices) {
                val button = buttonArray[i]
                val editText = editTextArray[i]

                button.setOnClickListener {
                    val value = editText.text.toString().toIntOrNull() ?: 0

                    val check = (1..100).random()
                    var message = ""
                    message = if (check <= value)
                        "Umiejętność: $value \nWylosowano: $check \nSukces"
                    else
                        "Umiejętność: $value \nWylosowano: $check \nPorażka"

                    val alertDialogBuilder = AlertDialog.Builder(requireContext())
                    alertDialogBuilder
                        .setTitle("Rzut na umiejętność")
                        .setMessage(message)
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }

        return view
    }


    private fun attachTextWatcher(editText: TextView, fieldName: String) {
        editText.addTextChangedListener { editable ->
            val input = editable.toString()
            val value = if (input.isNotBlank()) input.toIntOrNull() ?: 0 else 0
            sharedViewModel.updateCardField(fieldName, value)
        }
    }

    private fun attachCheckBoxListener(checkbox: CheckBox, fieldName: String) {
        checkbox.setOnCheckedChangeListener { _, isChecked ->
            sharedViewModel.updateCardField(fieldName, isChecked)
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


    private fun updateMaxMagia() {
        var value = mocEditText.text.toString().toIntOrNull() ?: 0
        if (id.isEmpty())
            poczytalnoscEditText.setText(value.toString())
        value /= 5
        if (id.isEmpty()) {
            magiaEditText.setText(value.toString())
        }
        maxMagiaTextView.text = value.toString()

    }

    private fun updateMaxZycie() {
        val kondycjaValue = kondycjaEditText.text.toString().toIntOrNull() ?: 0
        val bcialaValue = bcialaEditText.text.toString().toIntOrNull() ?: 0

        maxzycie = (kondycjaValue + bcialaValue) / 10
        maxZycieTextView.setText(maxzycie.toString())

        // Aktualizacja wartości w zycieEditText
        if (id.isEmpty())
            zycieEditText.setText(maxzycie.toString())
    }

    private fun updateRuch() {
        val silaValue = silaEditText.text.toString().toIntOrNull() ?: 0
        val bcialaValue = bcialaEditText.text.toString().toIntOrNull() ?: 0
        val zrecznoscValue = zrecznoscEditText.text.toString().toIntOrNull() ?: 0

        val ruchValue = when {
            (zrecznoscValue < bcialaValue && silaValue < bcialaValue) -> 7
            ((zrecznoscValue >= bcialaValue || silaValue >= bcialaValue) && !(zrecznoscValue > bcialaValue && silaValue > bcialaValue)) -> 8
            (zrecznoscValue == silaValue && silaValue == bcialaValue) -> 8
            (zrecznoscValue > bcialaValue && silaValue > bcialaValue) -> 9
            else -> 0 // wartość domyślna, jeśli żaden z warunków nie zostanie spełniony
        }

        ruchEditText.setText(ruchValue.toString())
    }


    private fun statHealthController() {
        val zycieValue = zycieEditText.text.toString().toIntOrNull() ?: 0
        val maxZycieValue = maxZycieTextView.text.toString().toIntOrNull() ?: 0

        if (zycieValue > maxZycieValue)
            zycieEditText.setText(maxZycieValue.toString())

    }

    private fun statMagicController() {
        val magiaValue = magiaEditText.text.toString().toIntOrNull() ?: 0
        val maxMagiaValue = maxMagiaTextView.text.toString().toIntOrNull() ?: 0

        if (magiaValue > maxMagiaValue)
            magiaEditText.setText(maxMagiaValue.toString())

    }


}

