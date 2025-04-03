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
import com.app.callofcthulhu.R
import com.app.callofcthulhu.utils.SharedViewModelInstance
import com.app.callofcthulhu.utils.ProfessionSingleton
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ProfessionPointsActivity : AppCompatActivity() {

    //private lateinit var sharedViewModel: SharedViewModel
    private var sila: Int = 0
    private var wyksztalcenie: Int = 0
    private var zrecznosc: Int = 0
    private lateinit var profesja: String
    var professionsFields: List<String> = listOf()
    var skillsFields: List<String> = listOf()

    //    val sharedViewModel = MyApp.sharedViewModel
    private lateinit var skillPoints: TextView

    val sharedViewModel = SharedViewModelInstance.instance

    var availablePoints: Int = 0
    var originalValues = mutableMapOf<EditText, Int>()

    private val fieldNamesMap = InterestPointsActivity().fieldNamesMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profession_points)




        skillPoints = findViewById(R.id.skills_points)

        skillPoints.text = availablePoints.toString()

        sila = intent.getIntExtra("sila", 0)
        wyksztalcenie = intent.getIntExtra("wyksztalcenie", 0)
        zrecznosc = intent.getIntExtra("zrecznosc", 0)
        profesja = intent.getStringExtra("profesja").toString()



        fetchSkillsFromFirestore()

    }


    private fun fetchSkillsFromFirestore() {
        val fireStore = FirebaseFirestore.getInstance()
        val skillsCollection = fireStore.collection("skills").document("skills")
        val profession = ProfessionSingleton.getProfessionInstance()

        skillsCollection.get().addOnSuccessListener { documentSnapshot ->
            skillsFields = getFieldsFromDocument(documentSnapshot)

            if (documentSnapshot.exists()) {
                val skillsData = documentSnapshot.data // Pobranie danych z dokumentu

                val commonFields =
                    profession?.umiejetnosciStale?.let {
                        skillsFields.intersect(it.toSet()).toList().sorted()
                    }
                val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)
                linearLayout?.removeAllViews()

                commonFields?.chunked(2) { chunk ->
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
                            val friendlyName = fieldNamesMap[fieldName] ?: fieldName
                            textView.text = friendlyName
                            textView.layoutParams = LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1.0f
                            )
                            textView.setPadding(20, 20, 20, 20)
                            textView.setTextColor(Color.BLACK)
                            val font = ResourcesCompat.getFont(
                                this,
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
                            editText.setBackgroundColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.transparentWhite
                                )
                            )
                            editText.hint = "Enter value"
                            val font2 = ResourcesCompat.getFont(
                                this,
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

            } else {
                // Obsługa przypadku gdy dokument nie istnieje
            }

            val commonStats = profession?.punktyStale

            commonStats?.forEach { (field, skillModifier) ->
                when (field) {
                    "sila" -> {

                        availablePoints += skillModifier * sila

                        skillPoints.text = availablePoints.toString()

                    }

                    "wyksztalcenie" -> {

                        availablePoints += skillModifier * wyksztalcenie

                        skillPoints.text = availablePoints.toString()


                    }

                    "zrecznosc" -> {

                        availablePoints += skillModifier * zrecznosc

                        skillPoints.text = availablePoints.toString()


                    }
                }


            }


        }.addOnFailureListener { exception ->
            // Obsługa błędów pobierania danych z Firestore
        }
    }


    private fun attachTextWatcher(editText: EditText, fieldName: String) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                val value = if (input.isNotBlank()) input.toIntOrNull() ?: 0 else 0
                sharedViewModel.updateSkillField(fieldName, value)
            }
        })
    }

    private fun setEditTextChangeListener(editText: EditText, minAllowedValue: Int) {
        originalValues[editText] = editText.text.toString().toIntOrNull() ?: 0

        val handler = Handler(Looper.getMainLooper())
        val delay: Long = 2000

        editText.addTextChangedListener(object : TextWatcher {
            private var runnable: Runnable? = null

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Niepotrzebne działanie przed zmianą
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Niepotrzebne działanie podczas zmiany
            }

            override fun afterTextChanged(s: Editable?) {
                // Usunięcie wszelkich wcześniejszych zadań, aby uniknąć wielokrotnego sprawdzania
                runnable?.let { handler.removeCallbacks(it) }

                runnable = Runnable {
                    val newValue = s.toString().toIntOrNull() ?: 0
                    val originalValue = originalValues[editText] ?: 0

                    val difference = newValue - originalValue

                    if (newValue in minAllowedValue..100) {
                        val newAvailablePoints = availablePoints - difference

                        if (newAvailablePoints >= 0) {
                            availablePoints = newAvailablePoints
                            originalValues[editText] = newValue
                        } else {
                            editText.setText(originalValues[editText].toString())
                        }
                    } else {
                        editText.setText(originalValues[editText].toString())
                    }

                    skillPoints.text = availablePoints.toString()
                }.also { handler.postDelayed(it, delay) }
            }
        })
    }

    private fun getFieldsFromDocument(document: DocumentSnapshot?): List<String> {
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