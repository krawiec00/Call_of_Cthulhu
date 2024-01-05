package com.app.callofcthulhu.fragments

import android.content.Intent
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
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.app.callofcthulhu.CardDetailsActivity
import com.app.callofcthulhu.InterestPointsActivity
import com.app.callofcthulhu.MyApp
import com.app.callofcthulhu.ProfessionPointsActivity
import com.app.callofcthulhu.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class StatsFragment2 : Fragment() {


    //private lateinit var sharedViewModel: SharedViewModel

    val id = CardDetailsActivity.docId
    var currentProfession: String? = null
    var availablePoints: Int = 0
    var originalValues = mutableMapOf<EditText, Int>()
    var check: Boolean = false
    val compareList: Array<String> = arrayOf("sila", "wyksztalcenie")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stats2, container, false)

        val sharedViewModel = MyApp.sharedViewModel




//        skillPoints.text = availablePoints.toString()

        //getFinalFields()


        val professionBtn = view.findViewById<Button>(R.id.klasa)

    professionBtn.setOnClickListener(){
        val intent = Intent(requireContext(), ProfessionPointsActivity::class.java)
        intent.putExtra("sila", sharedViewModel.card.value?.sila)
        intent.putExtra("wyksztalcenie", sharedViewModel.card.value?.wyksztalcenie)
        intent.putExtra("profesja", sharedViewModel.card.value?.profesja)
        startActivity(intent)
    }

        val zBtn = view.findViewById<Button>(R.id.zainteresowanie)

        zBtn.setOnClickListener(){
            val intent = Intent(requireContext(), InterestPointsActivity::class.java)
            intent.putExtra("inteligencja", sharedViewModel.card.value?.inteligencja)
            intent.putExtra("profesja", sharedViewModel.card.value?.profesja)
            startActivity(intent)
        }

        return view
    }


//    private fun getFinalFields() {
//        val db = FirebaseFirestore.getInstance()
//        val professionsCollection = db.collection("professions")
//        sharedViewModel.card.observe(viewLifecycleOwner) { card ->
//            val newProfessionName = card?.profesja ?: "defaultProfession"
//            if (currentProfession != newProfessionName && newProfessionName != "") {
//                currentProfession = newProfessionName
//
//                availablePoints = 0
//                skillPoints.text = availablePoints.toString()
//
//                professionsCollection.document(newProfessionName).get()
//                    .addOnSuccessListener { professionDocument ->
//                        val professionsFields = getFieldsFromDocument(professionDocument)
//
//                        db.collection("skills").document("skills").get()
//                            .addOnSuccessListener { skillsDocument ->
//                                val skillsFields = getFieldsFromDocument(skillsDocument)
//
//                                val commonFields =
//                                    skillsFields.intersect(professionsFields.toSet()).toList()
//
//
//                                val firestore = FirebaseFirestore.getInstance()
//                                val skillsRef = firestore.collection("skills").document("skills")
//
//                                skillsRef.get().addOnSuccessListener { documentSnapshot ->
//                                    if (documentSnapshot.exists()) {
//
//                                        val skillsData =
//                                            documentSnapshot.data // Pobranie danych z dokumentu
//                                        val linearLayout =
//                                            view?.findViewById<LinearLayout>(R.id.linearLayout)
//
//                                        linearLayout?.removeAllViews()
//
//                                        commonFields.forEach { fieldName ->
//                                            skillsData?.get(fieldName)?.let { fieldValue ->
//                                                val textView = TextView(context)
//                                                textView.text = fieldName // Ustawienie nazwy pola
//
//                                                val editText = EditText(context)
//                                                editText.setText(fieldValue.toString()) // Ustawienie wartości z Firestore
//
//
//                                                // Dodanie TextView i EditText do layoutu
//                                                linearLayout?.addView(textView)
//                                                linearLayout?.addView(editText)
//
//                                                // Dodanie TextWatcher do EditText
//                                                attachTextWatcher(editText, fieldName)
//                                                val fieldValueAsInt = fieldValue.toString().toIntOrNull()
//                                                if (fieldValueAsInt != null) {
//                                                    setEditTextChangeListener(
//                                                        editText,
//                                                        fieldValueAsInt
//                                                    )
//                                                }
//                                            }
//                                        }
//
//                                        val commonStats =
//                                            professionsFields.intersect(compareList.toSet()).toList()
//
//                                        var previousSila = 0
//                                        var previousWyksztalcenie = 0
//                                        Log.e("TEST", "COMMONSTATS: $commonStats")
//
//                                        sharedViewModel.card.observe(viewLifecycleOwner) { card ->
//                                            if (card != null) {
//                                            commonStats.forEach { field ->
//                                                Log.e("TEST", "FIELD: $field")
//                                                when (field) {
//                                                    "sila" -> {
//                                                        val value = card?.sila ?: 0
//                                                        if (value != previousSila) {
//
//                                                            professionsCollection.document(
//                                                                currentProfession!!
//                                                            )
//                                                                .get()
//                                                                .addOnSuccessListener { professionDocument ->
//                                                                    val skillModifier =
//                                                                        professionDocument?.get(
//                                                                            field
//                                                                        ) as? Long // Zmień typ na odpowiadający
//                                                                    val skillModifierInt =
//                                                                        skillModifier?.toInt()
//
//                                                                    val previousValueChange =
//                                                                        previousSila * (skillModifierInt
//                                                                            ?: 0)
//                                                                    val newValueChange =
//                                                                        value * (skillModifierInt
//                                                                            ?: 0)
//
//                                                                    availablePoints -= previousValueChange
//                                                                    availablePoints += newValueChange
//                                                                    skillPoints.text =
//                                                                        availablePoints.toString()
//                                                                    Log.e("TEST", "SIŁĄ")
//                                                                    previousSila = value
//                                                                }
//                                                        }
//                                                    }
//
//                                                    "wyksztalcenie" -> {
//                                                        val value = card?.wyksztalcenie ?: 0
//                                                        if (value != previousWyksztalcenie) {
//
//
//                                                            professionsCollection.document(
//                                                                currentProfession!!
//                                                            )
//                                                                .get()
//                                                                .addOnSuccessListener { professionDocument ->
//                                                                    val skillModifier =
//                                                                        professionDocument?.get(
//                                                                            field
//                                                                        ) as? Long // Zmień typ na odpowiadający
//                                                                    val skillModifierInt =
//                                                                        skillModifier?.toInt()
//
//                                                                    val previousValueChange =
//                                                                        previousWyksztalcenie * (skillModifierInt
//                                                                            ?: 0)
//                                                                    val newValueChange =
//                                                                        value * (skillModifierInt
//                                                                            ?: 0)
//                                                                    availablePoints -= previousValueChange
//                                                                    availablePoints += newValueChange
//                                                                    skillPoints.text =
//                                                                        availablePoints.toString()
//                                                                    Log.e("TEST", "WYKSZTAŁCENIE")
//                                                                    previousWyksztalcenie = value
//
//                                                                }
//
//
//                                                        }
//                                                    }
//
//
//                                                }
//                                            }
//
//                                            }
//                                        }
//
//                                    } else {
//                                        // Obsługa przypadku gdy dokument nie istnieje
//                                    }
//                                }.addOnFailureListener { exception ->
//                                    // Obsługa błędów pobierania danych z Firestore
//                                }
//
//
//                            }
//                    }
//            }
//        }}



//    private fun setEditTextChangeListener(editText: EditText, minAllowedValue: Int) {
//        originalValues[editText] = editText.text.toString().toIntOrNull() ?: 0
//
//        val handler = Handler(Looper.getMainLooper())
//        val delay: Long = 1000 // Opóźnienie w milisekundach (tutaj 1000ms = 1s)
//
//        editText.addTextChangedListener(object : TextWatcher {
//            private var changed = false
//            private var lastValidValue = originalValues[editText] ?: 0
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                // Niepotrzebne działanie przed zmianą
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                // Niepotrzebne działanie podczas zmiany
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                if (!changed) {
//                    changed = true
//                    handler.removeCallbacksAndMessages(null) // Usunięcie wcześniejszych zadań handlera
//
//                    handler.postDelayed({
//                        val newValue = s.toString().toIntOrNull() ?: 0
//                        val originalValue = originalValues[editText] ?: 0
//
//                        val difference = newValue - originalValue
//
//                        if (newValue in minAllowedValue..100) {
//                            val newAvailablePoints = availablePoints - difference
//
//                            if (newAvailablePoints >= 0) {
//                                availablePoints = newAvailablePoints
//                                originalValues[editText] = newValue
//                                lastValidValue = newValue
//                            } else {
//                                editText.setText(lastValidValue.toString())
//                            }
//                        } else {
//                            editText.setText(lastValidValue.toString())
//                        }
//
//                        if (availablePoints < 0) {
//                            availablePoints += difference
//                        }
//                        skillPoints.text = availablePoints.toString()
//                        changed = false
//                    }, delay)
//                }
//            }
//        })
//    }




//    fun fetchSkillsFromFirestore() {
//        val firestore = FirebaseFirestore.getInstance()
//        val skillsRef = firestore.collection("skills").document("skills")
//
//        skillsRef.get().addOnSuccessListener { documentSnapshot ->
//            if (documentSnapshot.exists()) {
//                val skillsData = documentSnapshot.data // Pobranie danych z dokumentu
//
//
//                val linearLayout = view?.findViewById<LinearLayout>(R.id.linearLayout)
//
//                skillsData?.forEach { (fieldName, fieldValue) ->
//                    val textView = TextView(context)
//                    textView.text = fieldName // Ustawienie nazwy pola
//
//                    val editText = EditText(context)
//                    editText.setText(fieldValue.toString()) // Ustawienie wartości z Firestore
//
//                    // Dodanie TextWatcher do EditText
//                    attachTextWatcher(editText, fieldName)
//
//                    // Dodanie TextView i EditText do layoutu
//                    linearLayout?.addView(textView)
//                    linearLayout?.addView(editText)
//                }
//            } else {
//                // Obsługa przypadku gdy dokument nie istnieje
//            }
//        }.addOnFailureListener { exception ->
//            // Obsługa błędów pobierania danych z Firestore
//        }
//    }

    // Przykładowa implementacja funkcji attachTextWatcher
//    private fun attachTextWatcher(editText: EditText, fieldName: String) {
//        editText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(s: Editable?) {
//                val input = s.toString()
//                val value = if (input.isNotBlank()) input.toIntOrNull() ?: 0 else 0
//                sharedViewModel.updateCardField(fieldName, value)
//            }
//        })
//    }


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


}

