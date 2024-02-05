package com.app.callofcthulhu.view.profession

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import com.app.callofcthulhu.R
import com.app.callofcthulhu.model.data.Profession
import com.app.callofcthulhu.utils.ProfessionSingleton
import com.app.callofcthulhu.utils.SharedViewModelInstance
import com.app.callofcthulhu.utils.Utility
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.atomic.AtomicInteger

class ProfessionDetails : AppCompatActivity() {

    private var db = FirebaseFirestore.getInstance()
    private var profesja: Profession? = Profession()
    private val spinnerSelections = mutableMapOf<Int, String>()
    var sharedViewModel = SharedViewModelInstance.instance
    private val fieldNamesMap = Utility.fieldNamesMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profession_details)

        val documentId = intent.getStringExtra("documentId")
        val button = findViewById<Button>(R.id.updateProf)

        db = FirebaseFirestore.getInstance()
        if (documentId != null) {
            db.collection("professions").document(documentId)
                .get()
                .addOnSuccessListener { dokument ->
                    profesja = dokument.toObject(Profession::class.java)
                    profesja?.let {
                        generujUI(it)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error getting document", e)
                }
        }

        button.setOnClickListener {
            profesja?.let { it1 -> ProfessionSingleton.setProfessionInstance(it1) }
            if (documentId != null) {
                sharedViewModel.updateProfessionId(documentId)
            }
            Toast.makeText(this, "Wybrano profesję", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    fun generujUI(profesja: Profession) {
        val containerPunktyZmienne = findViewById<LinearLayout>(R.id.containerPunktyZmienne)
        val containerUmiejetnosciZmienne =
            findViewById<LinearLayout>(R.id.containerUmiejetnosciZmienne)
        val containerUmiejetnosciDowolne =
            findViewById<LinearLayout>(R.id.containerUmiejetnosciDowolne)

        // Punkty zmienne - generuj CheckBoxy tylko jeśli lista nie jest pusta
        if (profesja.punktyZmienne.isNotEmpty()) {
            generujCheckBoxyDlaPunktowZmiennych(
                containerPunktyZmienne,
                profesja.punktyZmienne,
                profesja
            )

        } else {
            containerPunktyZmienne.visibility = View.GONE
            val text1 = findViewById<TextView>(R.id.labelPunktyZmienne)
            text1.visibility = View.GONE
        }

        // Umiejętności zmienne - generuj CheckBoxy tylko jeśli lista nie jest pusta
        if (profesja.umiejetnosciZmienne.isNotEmpty()) {
            generujCheckBoxyDlaUmiejetnosciZmiennych(
                containerUmiejetnosciZmienne,
                profesja.umiejetnosciZmienne,
                profesja.ileZmiennychUmiejetnosci,
                profesja
            )

        } else {
            containerUmiejetnosciZmienne.visibility = View.GONE
            val text2 = findViewById<TextView>(R.id.labelUmiejetnosciZmienne)
            text2.visibility = View.GONE
        }

        // Umiejętności dowolne - generuj Spinner tylko jeśli jest więcej niż 0
        if (profesja.umiejetnosciDowolne > 0) {
            db.collection("skills").document("skills").get()
                .addOnSuccessListener { dokument ->
                    if (dokument != null) {
                        val nazwyPol = dokument.data?.keys?.toList() ?: listOf()
                        var lista = nazwyPol.subtract(profesja.umiejetnosciStale.toSet()).toList()
                        lista = lista.subtract(profesja.umiejetnosciZmienne.toSet()).toList()
                        generujSpinnerDlaUmiejetnosciDowolnych(
                            containerUmiejetnosciDowolne,
                            lista,
                            profesja.umiejetnosciDowolne,
                            profesja
                        )
                    }
                }

        } else {
            containerUmiejetnosciDowolne.visibility = View.GONE
            val text3 = findViewById<TextView>(R.id.labelUmiejetnosciDowolne)
            text3.visibility = View.GONE
        }
    }

    fun generujCheckBoxyDlaUmiejetnosciZmiennych(
        container: ViewGroup,
        umiejetnosci: List<String>,
        ile: Int,
        profesja: Profession
    ) {
        val selectedCount = AtomicInteger()

        umiejetnosci.forEach { umiejetnosc ->
            val checkBox = CheckBox(this)
            val displayName = fieldNamesMap[umiejetnosc] ?: umiejetnosc
            checkBox.text = displayName
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    val currentCount = selectedCount.incrementAndGet()

                    if (currentCount <= ile) {
                        // Dodaj do listy umiejętności stałych, jeśli zaznaczono i nie przekracza limitu
                        profesja.umiejetnosciStale.add(umiejetnosc)
                    } else {
                        // Jeśli przekroczono limit, cofnij zaznaczenie i nie dodawaj do listy
                        checkBox.isChecked = false
                        selectedCount.decrementAndGet()
                        Toast.makeText(
                            this,
                            "Możesz wybrać maksymalnie $ile umiejętności",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Usuń z listy umiejętności stałych, jeśli odznaczono
                    selectedCount.decrementAndGet()
                    profesja.umiejetnosciStale.remove(umiejetnosc)
                }
            }
            container.addView(checkBox)
        }
    }

    // Zdefiniuj listę, która będzie przechowywać wybrane umiejętności
    val selectedSkills = mutableListOf<String>()

    fun generujSpinnerDlaUmiejetnosciDowolnych(
        container: ViewGroup,
        listaUmiejetnosci: List<String>,
        ile: Int,
        profesja: Profession
    ) {
        repeat(ile) { index ->
            val spinner = Spinner(this)
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                listaUmiejetnosci.map { fieldNamesMap[it] ?: it }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val wybranaUmiejetnosc = listaUmiejetnosci[position]

                    // Sprawdź, czy wybrana umiejętność została już wybrana w innym spinnerze
                    if (selectedSkills.contains(wybranaUmiejetnosc)) {
                        // Jeśli tak, cofnij wybór
                        spinner.setSelection(0)

                    } else {
                        // Usuń poprzednio wybraną umiejętność dla tego spinnera
                        spinnerSelections[index]?.let { prevSelection ->
                            profesja.umiejetnosciStale.remove(prevSelection)
                        }

                        // Aktualizuj mapowanie dla bieżącego spinnera
                        spinnerSelections[index] = wybranaUmiejetnosc

                        // Dodaj nowo wybraną umiejętność, jeśli nie istnieje
                        if (!profesja.umiejetnosciStale.contains(wybranaUmiejetnosc)) {
                            profesja.umiejetnosciStale.add(wybranaUmiejetnosc)
                            // Dodaj do listy wybranych umiejętności
                            selectedSkills.add(wybranaUmiejetnosc)
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            container.addView(spinner)
        }
    }


    fun generujCheckBoxyDlaPunktowZmiennych(
        container: ViewGroup,
        punktyStaleDostepne: Map<String, Int>,
        profesja: Profession
    ) {
        punktyStaleDostepne.forEach { (nazwa, wartosc) ->
            val checkBox = CheckBox(this)
            val displayName = fieldNamesMap[nazwa] ?: nazwa
            checkBox.text = displayName
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Usuń wszystkie inne zaznaczenia i aktualizuj model danych
                    container.children.filter { it is CheckBox && it != checkBox }
                        .forEach {
                            (it as CheckBox).isChecked = false
                            profesja.punktyStale.remove(
                                fieldNamesMap[it.text.toString()] ?: it.text.toString()
                            )
                        }
                    // Dodaj zaznaczony punkt do mapy punktyStale w modelu
                    profesja.punktyStale[nazwa] = wartosc
                } else {
                    // Usuń odznaczony punkt z mapy
                    profesja.punktyStale.remove(fieldNamesMap[nazwa] ?: nazwa)
                }
            }
            container.addView(checkBox)
        }
    }
}
