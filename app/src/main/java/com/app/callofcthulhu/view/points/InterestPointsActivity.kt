package com.app.callofcthulhu.view.points

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.app.callofcthulhu.utils.MyApp
import com.app.callofcthulhu.R
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class InterestPointsActivity : AppCompatActivity() {

    private var inteligencja: Int = 0

    private lateinit var profesja: String
    var professionsFields: List<String> = listOf()
    var skillsFields: List<String> = listOf()
    val sharedViewModel = MyApp.sharedViewModel
    private lateinit var skillPoints: TextView

    var availablePoints: Int = 0
    var originalValues = mutableMapOf<EditText, Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest_points)


        skillPoints = findViewById(R.id.skills_points)

        skillPoints.text = availablePoints.toString()

        inteligencja = intent.getIntExtra("inteligencja", 0)
        profesja = intent.getStringExtra("profesja").toString()


        fetchSkillsFromFirestore()

    }

    fun fetchSkillsFromFirestore() {
        val fireStore = FirebaseFirestore.getInstance()
        val skillsRef = fireStore.collection("skills").document("skills")

        val professionCollection = fireStore.collection("professions").document(profesja)

        professionCollection.get().addOnSuccessListener { professionDocument ->
            professionsFields = getFieldsFromDocument(professionDocument)




            skillsRef.get().addOnSuccessListener { documentSnapshot ->

                skillsFields = getFieldsFromDocument(documentSnapshot)


                if (documentSnapshot.exists()) {
                    val skillsData = documentSnapshot.data // Pobranie danych z dokumentu

                    val differentFields =
                        skillsFields.subtract(professionsFields.toSet()).toList().sorted()


                    val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)
                    linearLayout?.removeAllViews()

                    differentFields.chunked(2) { chunk ->
                        val horizontalLinearLayout = LinearLayout(this)
                        horizontalLinearLayout.orientation = LinearLayout.HORIZONTAL
                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        layoutParams.setMargins(0, 0, 0, 15)
                        horizontalLinearLayout.layoutParams = layoutParams

                        chunk.forEach { fieldName ->
                            skillsData?.get(fieldName)?.let { fieldValue ->
                                val textView = TextView(this)
                                textView.textSize = 16f
                                textView.text = fieldName.replace("_", " ") // Ustawienie nazwy pola
                                textView.layoutParams = LinearLayout.LayoutParams(
                                    0,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1.0f
                                )
                                textView.setPadding(20, 20, 20, 20)
                                textView.setTextColor(Color.BLACK)
                                val font = ResourcesCompat.getFont(this,
                                    R.font.im_fel_english_regular
                                )
                                textView.typeface = font

                                val editText = EditText(this)
                                editText.layoutParams = LinearLayout.LayoutParams(
                                    0,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1.0f
                                )
                                editText.textSize = 18f
                                editText.inputType = InputType.TYPE_CLASS_NUMBER
                                editText.gravity = Gravity.CENTER
                                editText.setBackgroundColor(Color.LTGRAY)
                                editText.setBackgroundColor(ContextCompat.getColor(this,
                                    R.color.transparentWhite
                                ))
                                editText.hint = "Enter value"
                                val font2 = ResourcesCompat.getFont(this,
                                    R.font.old_standard_tt_regular
                                )
                                editText.typeface = font2
                                editText.setText(fieldValue.toString()) // Ustawienie wartości z Firestore

                                horizontalLinearLayout.addView(textView)
                                horizontalLinearLayout.addView(editText)

                                // Dodanie TextWatcher do EditText
                                attachTextWatcher(editText, fieldName)
                                val fieldValueAsInt = fieldValue.toString().toIntOrNull()
                                if (fieldValueAsInt != null) {
                                    setEditTextChangeListener(
                                        editText,
                                        fieldValueAsInt
                                    )
                                }
                            }
                        }
                        linearLayout?.addView(horizontalLinearLayout)
                    }

                    availablePoints = inteligencja * 2
                    skillPoints.text = availablePoints.toString()


                } else {
                    // Obsługa przypadku gdy dokument nie istnieje
                }


            }.addOnFailureListener { exception ->
                // Obsługa błędów pobierania danych z Firestore
            }
        }
    }

    private fun attachTextWatcher(editText: EditText, fieldName: String) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                val value = if (input.isNotBlank()) input.toIntOrNull() ?: 0 else 0
                sharedViewModel.updateCardField(fieldName, value)
            }
        })
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