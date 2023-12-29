package com.app.callofcthulhu.fragments

import com.app.callofcthulhu.SharedViewModel
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.app.callofcthulhu.CardDetailsActivity
import com.app.callofcthulhu.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class StatsFragment2 : Fragment() {


    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var editTextArray: Array<EditText>
    private lateinit var Korzystanie_z_Bibliotek: LinearLayout
    private lateinit var Bron_Palna: LinearLayout
    private lateinit var Wiedza_o_Naturze: LinearLayout
    private lateinit var Sztuka_Przetrwania: LinearLayout
    private lateinit var Spostrzegawczosc: LinearLayout
    private lateinit var Psychologia: LinearLayout
    private lateinit var Pierwsza_Pomoc: LinearLayout
    private lateinit var Perswazja: LinearLayout
    private lateinit var Nawigacja: LinearLayout
    private lateinit var Nasluchiwanie: LinearLayout
    private lateinit var skillPoints: TextView

    var currentProfession: String? = null
    var linearLayouts = mutableListOf<LinearLayout>()
    val id = CardDetailsActivity.docId

    val compareList: Array<String> = arrayOf("sila", "wyksztalcenie")

    var availablePoints = 165
    var originalValues = mutableMapOf<EditText, Int>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stats2, container, false)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]




        Korzystanie_z_Bibliotek = view.findViewById(R.id.Korzystanie_z_Bibliotek)
        Bron_Palna = view.findViewById(R.id.Bron_Palna)
        Wiedza_o_Naturze = view.findViewById(R.id.Wiedza_o_Naturze)
        Sztuka_Przetrwania = view.findViewById(R.id.Sztuka_Przetrwania)
        Spostrzegawczosc = view.findViewById(R.id.Spostrzegawczosc)
        Psychologia = view.findViewById(R.id.Psychologia)
        Pierwsza_Pomoc = view.findViewById(R.id.Pierwsza_Pomoc)
        Perswazja = view.findViewById(R.id.Perswazja)
        Nawigacja = view.findViewById(R.id.Nawigacja)
        Nasluchiwanie = view.findViewById(R.id.Nasluchiwanie)

        skillPoints = view.findViewById(R.id.skills_points)

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



        linearLayouts = mutableListOf(
            Bron_Palna,
            Korzystanie_z_Bibliotek,
            Nasluchiwanie,
            Nawigacja,
            Perswazja,
            Pierwsza_Pomoc,
            Psychologia,
            Spostrzegawczosc,
            Sztuka_Przetrwania,
            Wiedza_o_Naturze
        )


        val db = FirebaseFirestore.getInstance()
        val professionsCollection = db.collection("professions")


        fun getFieldsFromDocument(document: DocumentSnapshot?): List<String> {
            val fields = mutableListOf<String>()
            document?.let {
                val dataMap = it.data
                dataMap?.keys?.forEach { key ->
                    fields.add(key)
                }
            }
            return fields
        }

        // Funkcja do porównania i ustawienia widoczności LinearLayouts
        fun compareAndSetLinearLayoutsVisibility(
            commonFields: List<String>,
            differentFields: List<String>,
            linearLayouts: List<LinearLayout>
        ) {
            linearLayouts.forEach { layout ->
                val tag = layout.tag as? String
                tag?.let { field ->
                    if (field in commonFields) {
                        layout.visibility = View.VISIBLE
                    } else if (field in differentFields) {
                        layout.visibility = View.GONE
                    }
                }
            }
        }





// Obserwowanie zmian w karcie
        sharedViewModel.card.observe(viewLifecycleOwner) { card ->
            val newProfessionName = card?.profesja ?: "Detektyw Policyjny"
            if (currentProfession != newProfessionName) {
                currentProfession = newProfessionName


                professionsCollection.document(newProfessionName).get()
                    .addOnSuccessListener { professionDocument ->
                        val professionsFields = getFieldsFromDocument(professionDocument)

                        db.collection("skills").document("skills").get()
                            .addOnSuccessListener { skillsDocument ->
                                val skillsFields = getFieldsFromDocument(skillsDocument)
                                Log.e("TEST", "POLA TO: $professionsFields")
                                val commonFields =
                                    skillsFields.intersect(professionsFields.toSet()).toList()
                                val differentFields =
                                    skillsFields.subtract(professionsFields.toSet()).toList()

                                compareAndSetLinearLayoutsVisibility(
                                    commonFields,
                                    differentFields,
                                    linearLayouts
                                )



                                linearLayouts.forEachIndexed { index, layout ->
                                    layout.viewTreeObserver.addOnGlobalLayoutListener {
                                        if (layout.visibility == View.VISIBLE) {
                                            val editText = editTextArray[index]
                                            attachTextWatcher(editText, layout.tag as String)
                                        }
                                    }
                                }

                                readData()

                                val commonStats = professionsFields.intersect(compareList.toSet()).toList()
                                var previousSila = 0
                                var previousWyksztalcenie = 0
                                commonStats.forEach { field ->
                                    sharedViewModel.card.observe(viewLifecycleOwner) { card ->
                                        when (field) {
                                            "sila" -> {
                                                val value = card?.sila ?: 0
                                                if (value != previousSila) {

                                                    professionsCollection.document(newProfessionName).get()
                                                        .addOnSuccessListener { professionDocument ->
                                                            val skillModifier = professionDocument?.get(field) as? Long // Zmień typ na odpowiadający
                                                            val skillModifierInt = skillModifier?.toInt()

                                                            val previousValueChange = previousSila * (skillModifierInt ?: 0)
                                                            val newValueChange = value * (skillModifierInt ?: 0)

                                                            availablePoints -= previousValueChange
                                                            availablePoints += newValueChange

                                                            previousSila = value
                                                            readData()
                                                        }
                                                }
                                            }

                                            "wyksztalcenie" -> {
                                                val value = card?.wyksztalcenie ?: 0
                                                if(value!=previousWyksztalcenie){



                                                    professionsCollection.document(newProfessionName).get()
                                                        .addOnSuccessListener { professionDocument ->
                                                            val skillModifier = professionDocument?.get(field) as? Long // Zmień typ na odpowiadający
                                                            val skillModifierInt = skillModifier?.toInt()

                                                            val previousValueChange = previousWyksztalcenie * (skillModifierInt ?: 0)
                                                            val newValueChange = value * (skillModifierInt ?: 0)
                                                            availablePoints -= previousValueChange
                                                            availablePoints += newValueChange


                                                            previousWyksztalcenie = value
                                                            readData()

                                                        }


                                                }
                                            }
                                            // Dodaj obsługiwanie innych pól z commonStats
                                        }
                                    }
                                }

                            }.addOnFailureListener { e ->
                                Log.e("Firestore", "Error fetching skills document: $e")
                                // Obsługa błędu pobierania dokumentu 'skills'
                            }
                    }.addOnFailureListener { e ->
                        Log.e("Firestore", "Error fetching skills document: $e")
                        // Obsługa błędu pobierania dokumentu 'skills'
                    }
            }
        }


        val newFragment = StatsFragment3()

// Rozpoczęcie transakcji fragmentu
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

// Zastąpienie aktualnego fragmentu nowym fragmentem
        transaction.replace(R.id.container, newFragment)

// Dodanie transakcji do back stack, aby można było wrócić do poprzedniego fragmentu przy naciśnięciu przycisku cofania
       // transaction.addToBackStack(null)

// Zatwierdzenie transakcji
        transaction.commit()




        setEditTextChangeListener(editTextArray[0],20)
        setEditTextChangeListener(editTextArray[1],20)
        setEditTextChangeListener(editTextArray[2],20)
        setEditTextChangeListener(editTextArray[3],10)
        setEditTextChangeListener(editTextArray[4],10)
        setEditTextChangeListener(editTextArray[5],30)
        setEditTextChangeListener(editTextArray[6],10)
        setEditTextChangeListener(editTextArray[7],25)
        setEditTextChangeListener(editTextArray[8],10)
        setEditTextChangeListener(editTextArray[9],10)


        return view
    }


    private fun setEditTextChangeListener(editText: EditText, minAllowedValue: Int) {
        originalValues[editText] = editText.text.toString().toIntOrNull() ?: 0

        val handler = Handler(Looper.getMainLooper())
        val delay: Long = 1000 // Opóźnienie w milisekundach (tutaj 1000ms = 1s)

        editText.addTextChangedListener(object : TextWatcher {
            private var changed = false
            private var lastValidValue = originalValues[editText] ?: 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Niepotrzebne działanie przed zmianą
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Niepotrzebne działanie podczas zmiany
            }

            override fun afterTextChanged(s: Editable?) {
                if (!changed) {
                    changed = true
                    handler.removeCallbacksAndMessages(null) // Usunięcie wcześniejszych zadań handlera

                    handler.postDelayed({
                        val newValue = s.toString().toIntOrNull() ?: 0
                        val originalValue = originalValues[editText] ?: 0

                        val difference = newValue - originalValue

                        if (newValue in minAllowedValue..100) {
                            val newAvailablePoints = availablePoints - difference

                            if (newAvailablePoints >= 0) {
                                availablePoints = newAvailablePoints
                                originalValues[editText] = newValue
                                lastValidValue = newValue
                            } else {
                                editText.setText(lastValidValue.toString())
                            }
                        } else {
                            editText.setText(lastValidValue.toString())
                        }

                        if (availablePoints < 0) {
                            availablePoints += difference
                        }
                        skillPoints.text = availablePoints.toString()
                        changed = false
                    }, delay)
                }
            }
        })
    }




    private fun readData() {
        FirebaseFirestore.getInstance().collection("skills")
            .document("skills")
            .get()
            .addOnSuccessListener { document ->
                document?.let {
                    if (it.exists()) {
                        val fields = mapOf(
                            "Bron_Palna" to editTextArray[0],
                            "Korzystanie_z_Bibliotek" to editTextArray[1],
                            "Nasluchiwanie" to editTextArray[2],
                            "Nawigacja" to editTextArray[3],
                            "Perswazja" to editTextArray[4],
                            "Pierwsza_Pomoc" to editTextArray[5],
                            "Psychologia" to editTextArray[6],
                            "Spostrzegawczosc" to editTextArray[7],
                            "Sztuka_Przetrwania" to editTextArray[8],
                            "Wiedza_o_Naturze" to editTextArray[9]

                        )

                        fields.forEach { (key, editText) ->
                            val value = document.getString(key)
                            editText.setText(value ?: "") // Ustawienie wartości w polu EditText
                        }
                    }
                }
            }
    }

    private fun attachTextWatcher(editText: TextView, fieldName: String) {
        editText.addTextChangedListener { editable ->

            val input = editable.toString()
            val value = if (input.isNotBlank()) input.toIntOrNull() ?: 0 else 0
            sharedViewModel.updateCardField(fieldName, value)

        }
    }





}

